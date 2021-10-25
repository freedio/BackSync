package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.FileReceiver
import com.coradec.apps.backsync.ctrl.SyncWriter
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.core.model.Timespan
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.model.AgentPool
import com.coradec.coradeck.ctrl.module.CoraControl
import com.coradec.coradeck.text.model.LocalText
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class LocalReceiver(private val hostname: String, private val groupName: String = PROP_GROUP.value) : BasicAgent(), FileReceiver {
    private val medium: Path get() = PROP_MEDIUM.value
    private val baseDir get() = medium.resolve(groupName).resolve(hostname)
    private val pool = AgentPool(1, 20) { SyncWriter() }
    var active = true

    init {
        info(TEXT_INITIALIZING_RECEIVER)
        Files.createDirectories(baseDir)
    }

    override fun createRegularFile(request: RegularFileDiscovered) {
        if (!active) throw IllegalStateException("Receiver has been closed!")
        pool.accept(request.withBase(baseDir))
    }

    override fun createPhysicalDirectory(request: DirectoryDiscovered) {
        if (!active) throw IllegalStateException("Receiver has been closed!")
        pool.accept(request.withBase(baseDir))
    }

    override fun createSymbolicLink(request: SymbolicLinkDiscovered) {
        if (!active) throw IllegalStateException("Receiver has been closed!")
        pool.accept(request.withBase(baseDir))
    }

    override fun createLoopLink(request: LoopLinkDiscovered) {
        if (!active) throw IllegalStateException("Receiver has been closed!")
        pool.accept(request.withBase(baseDir))
    }

    override fun createLostLink(request: LostLinkDiscovered) {
        if (!active) throw IllegalStateException("Receiver has been closed!")
        pool.accept(request.withBase(baseDir))
    }

    override fun updateDirectory(request: DirectoryUpdate) {
        if (!active) throw IllegalStateException("Receiver has been closed!")
        pool.accept(request.withBase(baseDir))
    }

    override fun close() {
        IMMEX.standby(Timespan(20, TimeUnit.SECONDS))
        active = false
        pool.shutdown()
        info(TEXT_RECEIVER_CLOSED)
    }

    companion object {
        val IMMEX = CoraControl.IMMEX
        val PROP_MEDIUM = LocalProperty<Path>("Medium")
        val PROP_GROUP = LocalProperty<String>("Group")
        val TEXT_INITIALIZING_RECEIVER = LocalText("InitializingReceiver")
        val TEXT_RECEIVER_CLOSED = LocalText("ReceiverClosed")
    }

}
