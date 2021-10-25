package com.coradec.apps.backsync.com

import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.Companion.defaultPriority
import java.nio.file.Path
import java.nio.file.Paths

class ExclusionDiscovered(
    origin: Origin,
    priority: Priority = defaultPriority,
    val path: Path
) : DiscoveryRequest(origin, priority, Paths.get(""))
