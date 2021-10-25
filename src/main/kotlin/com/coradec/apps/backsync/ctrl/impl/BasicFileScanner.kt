package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.DiscoveryEndEvent
import com.coradec.apps.backsync.com.ExclusionDiscovered
import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.FileScanner
import com.coradec.apps.backsync.ctrl.Filter
import com.coradec.coradeck.com.model.Notification
import com.coradec.coradeck.com.model.Request
import com.coradec.coradeck.com.model.impl.BasicCommand
import com.coradec.coradeck.com.model.unto
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.*
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.module.CoraControl
import com.coradec.coradeck.ctrl.module.ignore
import com.coradec.coradeck.ctrl.module.receive
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.streams.toList

class BasicFileScanner(
    private val baseDir: Path,
    val filter: Filter
) : BasicAgent(), FileScanner {

    override fun execute(): Notification<DiscoveryEndEvent> {
        approve(WalkCommand::class)
        receive(WalkCommand::class)
        val end = Notification(DiscoveryEndEvent(here))
        accept(walk(baseDir)) andThen {
            IMMEX.inject(end)
            ignore()
        }
        return end
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun walk(path: Path): Request = when {
        path.isDirectory(NOFOLLOW_LINKS) -> {
            val dirInclude = !filter.excludes(path)
            val treeInclude = !filter.excludesSubtree(path)
            val entries = mutableListOf<Request>()
            if (dirInclude) entries += DirectoryDiscovered(this, path)
            if (treeInclude) entries += Files.list(path).map { WalkCommand(this, it) unto this }.use { fileStream ->
                CoraControl.createItemSet(
                    this,
                    fileStream.toList(),
                    this
                )
            }
            if (dirInclude) entries += DirectoryUpdate(this, path)
            CoraControl.createRequestList(this, entries)
        }
        path.isSymbolicLink() -> if (filter.excludes(path)) ExclusionDiscovered(this, B2, path) else {
            val linkTarget = path.resolve(path.readSymbolicLink())
            when {
                linkTarget.notExists() -> LostLinkDiscovered(this, path)
                path.startsWith(linkTarget) -> LoopLinkDiscovered(this, path)
                else -> SymbolicLinkDiscovered(this, path)
            }
        }
        path.isRegularFile(NOFOLLOW_LINKS) ->
            if (filter.excludes(path)) ExclusionDiscovered(this, path = path) else RegularFileDiscovered(this, path)
        else -> if (filter.excludes(path)) ExclusionDiscovered(this, path= path) else FreakDiscovered(this, B0, path)
    }

    inner class WalkCommand(origin: Origin, val path: Path) : BasicCommand(origin, B3) {
        override fun execute() {
            IMMEX.inject(walk(path) propagateTo this)
        }
    }

    companion object {
        val IMMEX = CoraControl.IMMEX
    }
}
