package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B1
import java.nio.file.Path

class LoopLinkDiscovered(origin: Origin, val path: Path, baseDir: Path = emptyBase) : DiscoveryRequest(origin, B1, baseDir)
