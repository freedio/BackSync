package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.Companion.defaultPriority
import java.nio.file.Path
import java.nio.file.Paths

open class DiscoveryRequest(
    origin: Origin,
    priority: Priority = defaultPriority,
    var baseDir: Path
): BasicRequest(origin, priority) {
    fun withBase(dir: Path): DiscoveryRequest = also { baseDir = dir }

    companion object {
        @JvmStatic protected val emptyBase = Paths.get("")
    }
}
