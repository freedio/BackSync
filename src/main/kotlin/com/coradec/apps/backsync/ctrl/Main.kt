package com.coradec.apps.backsync.ctrl

import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.model.LocalText
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.net.InetAddress
import java.nio.file.Path
import kotlin.system.exitProcess

open class Main: BasicAgent() {
    protected val hostname = InetAddress.getLocalHost().hostName
    protected val roots: List<Path> get() = PROP_ROOTS.value
    protected val filter: Filter get() = PROP_FILTER.value
    protected val system: BackendSystem get() = BackendSystem(PROP_SYSTEM.value)
    protected val writer: FileReceiver by lazy { FileReceiver(system, hostname) }

    companion object {
        val PROP_ROOTS = LocalProperty<List<Path>>("Roots")
        val PROP_FILTER = LocalProperty<Filter>("Filter")
        val PROP_SYSTEM = LocalProperty<String>("System")
        val TEXT_MISSING_FUNCTION = LocalText("MissingFunction0")
        private val TEXT_UNKNOWN_FUNCTION = LocalText("UnknownFunction1")

        operator fun invoke(function: String, args: List<String>) = when(function) {
//            "setup" -> Setup(args).execute()
            "upsync" -> UpSync(args).execute()
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