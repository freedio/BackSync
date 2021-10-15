package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class LoopLinkDiscovered(origin: Origin, val path: Path, baseDir: Path = emptyBase) : DiscoveryRequest(origin, baseDir)
