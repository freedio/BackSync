package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.Companion.defaultPriority
import java.nio.file.Path

class FreakDiscovered(origin: Origin, priority: Priority = defaultPriority, val path: Path) :
    DiscoveryRequest(origin, priority, path)
