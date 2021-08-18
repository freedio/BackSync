package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.SetupRequest
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.util.asOrigin
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.net.InetAddress
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

/**
 * Main class of the Setup process.
 */

fun main(vararg args: String) {
    CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
    Setup(args.toList()).run()
}

class Setup(args: List<String>) : Main() {
    private val backServer = BackServer(this, system)
    private val hostname: String = InetAddress.getLocalHost().hostName
    private val baseDir: Path = if (args.isEmpty()) root else Paths.get(args[0])

    fun run() {
        try {
            backServer.inject(SetupRequest(Setup::class.asOrigin, hostname, group, baseDir)).standBy()
            exitProcess(0)
        } catch (e: Exception) {
            error(e)
            exitProcess(1)
        }
    }
}