package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.model.LocalText
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
    if (args.isEmpty()) {
        Main.usage(Main.TEXT_MISSING_FUNCTION.content)
    }
    val arglist = mutableListOf(*args)
    while (arglist[0] in setOf("-?", "-h", "--help")) {
        Main.usage()
        arglist.removeAt(0)
    }
    val function = arglist.removeAt(0)
    Main(function, arglist)
}

open class Main(qsize: Int = 1024): BasicAgent(qsize) {
    val system: String get() = serverType.value
    val root: Path get() = rootDirectory.value
    val exclusions: Exclusions get() = excludes.value
    val group: String get() = backupGroup.value
    private val filelog get() = PrintWriter(Files.newBufferedWriter(fileLogFile.value))

    init {
        atEnd { filelog.close() }
    }

    companion object {
        val TEXT_MISSING_FUNCTION = LocalText("MissingFunction0")
        private val TEXT_UNKNOWN_FUNCTION = LocalText("UnknownFunction1")
        @JvmStatic protected val TEXT_STREAM_FAILED = LocalText("StreamFailed2")
        private val serverType = LocalProperty<String>("System")
        private val rootDirectory = LocalProperty<Path>("Root")
        private val excludes = LocalProperty<Exclusions>("Exclude")
        private val backupGroup = LocalProperty<String>("Group")
        private val fileLogFile = LocalProperty<Path>("FileLog")

        operator fun invoke(function: String, args: List<String>) = when(function) {
            "setup" -> Setup(args).run()
            "upsync" -> UpSync(args).run()
            else -> usage(TEXT_UNKNOWN_FUNCTION[function])
        }

        fun usage(problem: String? = null) {
            if (problem != null) {
                println(problem)
                println()
            }
            println("Usage: [-?] ‹function›[{ ‹arg›}]")
            println("where: -? prints this usage.")
            println("       ‹function› is the name of a BackSync function: \"setup\", \"upsync\", \"downsync\".")
            println("       ‹arg› is an argument required by the selected BackSync function.")
            if (problem != null) exitProcess(1)
        }
    }
}
