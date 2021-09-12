package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.*
import com.coradec.apps.backsync.com.impl.FileDiscoveryEndEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryEntryEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryErrorEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryPostDirectoryEvent
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.trouble.FatalException
import com.coradec.coradeck.core.util.asOrigin
import com.coradec.coradeck.ctrl.module.CoraControl.IMMEX
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.io.PrintWriter
import java.net.InetAddress
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

/**
 * Main class of the UpSync process.
 */

fun main(vararg args: String) {
    CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
    UpSync(args.toList()).run()
}

class UpSync(args: List<String>) : Main(65536) {
    private val log = PrintWriter(Files.newOutputStream(Paths.get("/tmp/UpSync.log")))
    private val backServer = BackServer(this, system)
    private val hostname = InetAddress.getLocalHost().hostName
    private val baseDir = if (args.isEmpty()) root else Paths.get(args[0])
    private val fileReader = FileReader()
    private var killFileReader = false

    init {
        addRoute(FileDiscoveryEntryEvent::class.java, ::discovered)
        addRoute(FileDiscoveryEndEvent::class.java, ::end)
        addRoute(FileDiscoveryErrorEvent::class.java, ::error)
        addRoute(FileDiscoveryPostDirectoryEvent::class.java, ::afterDir)
    }

    private fun discovered(event: FileDiscoveryEntryEvent) {
        if (!killFileReader) {
            val path = event.path
            log.println("Discovered $path")
            backServer.inject((
                    if (path.isDirectory()) UpSyncDirRequest(this::class.asOrigin, hostname, group, path)
                    else UpSyncRequest(this::class.asOrigin, hostname, group, path)
                    ).onFailure {
                    if (reason is FatalException) {
                        killFileReader = true
                        fileReader.stop(reason ?: RuntimeException("Unknown Reason"))
                    }
                }
            )
        }
    }

    private fun end(event: FileDiscoveryEndEvent) {
        trace("Received FileDiscoveryEndEvent.")
        detail("Agent Pool Statistics: %s", backServer.writerPool.stats)
        backServer.close()
        exitProcess(0)
    }

    private fun error(event: FileDiscoveryErrorEvent) {
        trace("Received FileDiscoveryErrorEvent.")
        if (event.problem != null) error(event.problem)
    }

    private fun afterDir(event: FileDiscoveryPostDirectoryEvent) {
        if (!killFileReader) {
            val path = event.directory
            log.println("Fixing $path")
            backServer.inject(UpSyncDirUpdateRequest(this::class.asOrigin, hostname, group, path))
        }
    }

    fun run() {
        val recipe = backServer.inject(DownloadRecipeVoucher(this, hostname, group)).value
        IMMEX.plugin(FileDiscoveryEvent::class, this)
        IMMEX.plugin(FileDiscoveryEndEvent::class, this)
        IMMEX.plugin(FileDiscoveryErrorEvent::class, this)
        IMMEX.plugin(FileDiscoveryPostDirectoryEvent::class, this)
        val count = fileReader.inject(StartDiscoveryVoucher(this, baseDir, recipe)).value
        debug("%d files processed.", count)
    }
}