package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.DiscoveryEndEvent
import com.coradec.apps.backsync.ctrl.impl.BasicFileScanner
import com.coradec.coradeck.com.model.Notification
import java.nio.file.Path

interface FileScanner {

    companion object {
        operator fun invoke(baseDir: Path, filter: Filter) = BasicFileScanner(baseDir, filter)
    }

    fun execute(): Notification<DiscoveryEndEvent>
}