package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.DiscoveryEndEvent
import com.coradec.apps.backsync.com.DiscoveryEvent
import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.impl.BasicFileScanner
import com.coradec.coradeck.com.model.MultiRequest
import com.coradec.coradeck.com.model.impl.Syslog
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.util.classname
import com.coradec.coradeck.ctrl.module.CoraControl
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.ctrl.module.ignore
import com.coradec.coradeck.ctrl.module.receive
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.model.LocalText
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

class UpSync(args: List<String>) : Main() {
    private val baseDirs: List<Path> = if (args.isEmpty()) roots else args.map { Paths.get(it) }

    init {
        addRoute(DirectoryDiscovered::class, ::directoryDiscovered)
        addRoute(DirectoryUpdate::class, ::updateDirectory)
        addRoute(SymbolicLinkDiscovered::class, this::symbolicLinkDiscovered)
        addRoute(LostLinkDiscovered::class, ::lostLinkDiscovered)
        addRoute(LoopLinkDiscovered::class, this::loopLinkDiscovered)
        addRoute(RegularFileDiscovered::class, ::regularFileDiscovered)
        addRoute(FreakDiscovered::class, ::freakDiscovered)
        addRoute(DiscoveryEndEvent::class, ::discoveryEnded)
        approve(MultiRequest::class)
    }

    private fun directoryDiscovered(request: DirectoryDiscovered) {
        writer.createPhysicalDirectory(request)
    }

    private fun updateDirectory(request: DirectoryUpdate) {
        writer.updateDirectory(request)
    }

    private fun symbolicLinkDiscovered(request: SymbolicLinkDiscovered) {
        writer.createSymbolicLink(request)
    }

    private fun lostLinkDiscovered(request: LostLinkDiscovered) {
        writer.createLostLink(request)
    }

    private fun loopLinkDiscovered(request: LoopLinkDiscovered) {
        writer.createLoopLink(request)
    }

    private fun regularFileDiscovered(request: RegularFileDiscovered) {
        writer.createRegularFile(request)
    }

    private fun freakDiscovered(request: FreakDiscovered) {
        info(TEXT_FREAK_DISCOVERED, request.path, fileTypeOf(request.path))
    }

    private fun discoveryEnded(event: DiscoveryEndEvent) {
        info(TEXT_DISCOVERY_ENDED)
        writer.close()
        IMMEX.standby()
        ignore()
        exitProcess(0)
    }

    fun execute() {
        receive(
            DiscoveryEvent::class,
            DirectoryUpdateEvent::class,
            DiscoveryEndEvent::class,
            DiscoveryRequest::class,
            MultiRequest::class
        )
        debug("Basedirs (%s): (%s)%s", baseDirs.classname, baseDirs[0].classname, baseDirs)
        baseDirs.forEach { baseDir ->
            BasicFileScanner(baseDir, filter).execute()
        }
    }

    companion object {
        val IMMEX = CoraControl.IMMEX
        val TEXT_FREAK_DISCOVERED = LocalText("FreakDiscovered2")
        val TEXT_DISCOVERY_ENDED = LocalText("DiscoveryEnded")
    }
}

fun main(vararg args: String) {
    CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
    try {
        UpSync(args.toList()).execute()
    } catch (e: Exception) {
        Syslog.error(e)
        exitProcess(1)
    }
}
