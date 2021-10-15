package com.coradec.apps.backsync.com

import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B3
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

open class FileDiscoveryEvent(origin: Origin, val file: Path, val attrs: BasicFileAttributes): DiscoveryEvent(origin, B3)
