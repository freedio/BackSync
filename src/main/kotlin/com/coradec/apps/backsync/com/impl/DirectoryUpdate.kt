package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.DiscoveryRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B2
import java.nio.file.Path

class DirectoryUpdate(origin: Origin, val path: Path, baseDir: Path = emptyBase) : DiscoveryRequest(origin, B2, baseDir)
