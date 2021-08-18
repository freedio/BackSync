package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.*
import com.coradec.coradeck.com.model.Information
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.trouble.FatalException
import com.coradec.coradeck.core.util.asOrigin
import com.coradec.coradeck.core.util.relax
import com.coradec.coradeck.core.util.swallow
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.net.InetAddress
import java.nio.file.Paths
import kotlin.system.exitProcess

/**
 * Main class of the UpSync process.
 */

fun main(vararg args: String) {
    CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
    UpSync(args.toList()).run()
}

class UpSync(args: List<String>) : Main() {
    private val backServer = BackServer(this, system)
    private val hostname = InetAddress.getLocalHost().hostName
    private val baseDir = if (args.isEmpty()) root else Paths.get(args[0])
    private val fileReader = FileReader(baseDir, exclusions, this, filelog)
    private var killFileReader = false

    override fun onMessage(message: Information) = when (message) {
        is BackServerReadyEvent -> fileReader.start().swallow()
        is FileDiscoveryEvent ->
            if (!killFileReader)
                backServer.inject(UpSyncRequest(this::class.asOrigin, hostname, group, message.descriptor).onFailure {
                    if (reason is FatalException) {
                        killFileReader = true
                        fileReader.stop(reason ?: RuntimeException("Unknown Reason"))
                    }
                }).swallow()
            else relax()
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
        backServer.start()
        debug("Starting file reader root=$baseDir, exclusions=$exclusions")
        fileReader.start()
    }
}