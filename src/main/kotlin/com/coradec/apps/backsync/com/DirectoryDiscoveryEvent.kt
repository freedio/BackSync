package com.coradec.apps.backsync.com

import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B1
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

open class DirectoryDiscoveryEvent(origin: Origin, val dir: Path, val attrs: BasicFileAttributes): DiscoveryEvent(origin, B1)
