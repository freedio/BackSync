package com.coradec.apps.backsync.main

import com.coradec.apps.backsync.ctrl.DownSync
import com.coradec.apps.backsync.ctrl.Restore
import com.coradec.apps.backsync.ctrl.UpSync
import com.coradec.coradeck.com.ctrl.impl.Logger
import com.coradec.coradeck.com.model.impl.Syslog
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import kotlin.system.exitProcess

fun main(vararg args: String) {
    CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
    var status = 0
    try {
        BackSync.start(*args)
    } catch (e: Throwable) {
        Syslog.error(e)
        status = 1
    } finally {
        exitProcess(status)
    }
}

object BackSync : Logger() {

    fun start(vararg args: String) {
        if (args.size == 0) usage() else when (args[0]) {
            "down" -> downSync()
            "up" -> upSync()
            "restore" -> restore(*(if (args.size > 1) args.drop(1) else listOf()).toTypedArray())
            else -> usage(args[0])
        }
    }

    /** Synchronize after startup. */
    private fun downSync() {
        DownSync.execute()
    }

    /** Synchronize before shutdown. */
    private fun upSync() {
        UpSync.execute().standBy()
    }

    /** Restore the specified files. */
    private fun restore(vararg arg: String?) {
        Restore(arg.toList()).execute()
    }

    private fun usage(arg: String = "") {
        if (arg.isNotBlank()) println("What do you mean by \"$arg\"?")
        println("Usage: BackSync (up|down|restore...)")
        println("where: up means to back up and sync up")
        println("       down means to sync down")
        println("       restore means to recover to a particular time stamp")
    }
}
