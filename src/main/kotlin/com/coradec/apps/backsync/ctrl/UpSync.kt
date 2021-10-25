package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.DiscoveryEndEvent
import com.coradec.apps.backsync.com.DiscoveryEvent
import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.apps.backsync.com.ExclusionDiscovered
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
import com.coradec.coradeck.module.model.CoraModules
import com.coradec.coradeck.text.model.LocalText
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

class UpSync(args: List<String>) : Main() {
    private val baseDirs: List<Path> = if (args.isEmpty()) roots else args.map { Paths.get(it) }
    private val requestLog = PrintWriter(FileWriter("/tmp/UpSyncRequests.log"))

    init {
        route(DirectoryDiscovered::class, ::directoryDiscovered)
        route(DirectoryUpdate::class, ::updateDirectory)
        route(SymbolicLinkDiscovered::class, this::symbolicLinkDiscovered)
        route(LostLinkDiscovered::class, ::lostLinkDiscovered)
        route(LoopLinkDiscovered::class, this::loopLinkDiscovered)
        route(RegularFileDiscovered::class, ::regularFileDiscovered)
        route(FreakDiscovered::class, ::freakDiscovered)
        route(DiscoveryEndEvent::class, ::discoveryEnded)
        route(ExclusionDiscovered::class, ::exclusionDiscovered)
        approve(MultiRequest::class)
    }

    private fun exclusionDiscovered(request: ExclusionDiscovered) {
        val path = request.path
        info(TEXT_EXCLUSION_DISCOVERED, path, fileTypeOf(path))
        requestLog.println("− $path")
        request.succeed()
    }

    private fun directoryDiscovered(request: DirectoryDiscovered) {
        val path = request.path
        requestLog.println("+ directory: $path")
        writer.createPhysicalDirectory(request)
    }

    private fun updateDirectory(request: DirectoryUpdate) {
        val path = request.path
        requestLog.println("• directory: $path")
        writer.updateDirectory(request)
    }

    private fun symbolicLinkDiscovered(request: SymbolicLinkDiscovered) {
        val path = request.path
        requestLog.println("+ symlink: $path")
        writer.createSymbolicLink(request)
    }

    private fun lostLinkDiscovered(request: LostLinkDiscovered) {
        val path = request.path
        requestLog.println("+ lost link: $path")
        writer.createLostLink(request)
    }

    private fun loopLinkDiscovered(request: LoopLinkDiscovered) {
        val path = request.path
        requestLog.println("+ loop link: $path")
        writer.createLoopLink(request)
    }

    private fun regularFileDiscovered(request: RegularFileDiscovered) {
        val path = request.path
        requestLog.println("+ file: $path")
        writer.createRegularFile(request)
    }

    private fun freakDiscovered(request: FreakDiscovered) {
        val path = request.path
        requestLog.println("− freak: $path")
        info(TEXT_FREAK_DISCOVERED, request.path, fileTypeOf(request.path))
        request.succeed()
    }

    private fun discoveryEnded(event: DiscoveryEndEvent) {
        info(TEXT_DISCOVERY_ENDED)
        writer.close()
        requestLog.println("/ End")
        requestLog.close()
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
        val TEXT_EXCLUSION_DISCOVERED = LocalText("ExclusionDiscovered1")
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
