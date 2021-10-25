package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B0
import java.nio.file.Path

class DirectoryDiscovered(origin: Origin, val path: Path, baseDir: Path = emptyBase) : DiscoveryRequest(origin, B0, baseDir)
