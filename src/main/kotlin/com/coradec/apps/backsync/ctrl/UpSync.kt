package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.*
import com.coradec.apps.backsync.com.impl.FileDiscoveryEndEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryEntryEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryErrorEvent
import com.coradec.coradeck.com.model.Information
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.trouble.FatalException
import com.coradec.coradeck.core.util.asOrigin
import com.coradec.coradeck.core.util.relax
import com.coradec.coradeck.core.util.swallow
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

    override fun onMessage(message: Information) = when (message) {
        is FileDiscoveryEntryEvent ->
            if (!killFileReader) {
                log.println(message.path)
                backServer.inject((
                        if (message.path.isDirectory()) UpSyncDirRequest(this::class.asOrigin, hostname, group, message.path)
                        else UpSyncRequest(this::class.asOrigin, hostname, group, message.path)
                        ).onFailure {
                        if (reason is FatalException) {
                            killFileReader = true
                            fileReader.stop(reason ?: RuntimeException("Unknown Reason"))
                        }
                    }).swallow()
            } else relax()
        is FileDiscoveryEndEvent -> {
            debug("Received FileDiscoveryEndEvent.")
            backServer.close()
            exitProcess(0)
        }
        is FileDiscoveryErrorEvent -> {
            debug("Received FileDiscoveryErrorEvent.")
            if (message.problem != null) error(message.problem)
            relax()
        }
        else -> super.onMessage(message)
    }

    fun run() {
        val recipe = backServer.inject(DownloadRecipeVoucher(this, hostname, group)).value
        IMMEX.plugin(FileDiscoveryEvent::class, this)
        val count = fileReader.inject(StartDiscoveryVoucher(this, root, recipe)).value
        debug("%d files processed.", count)
    }
}