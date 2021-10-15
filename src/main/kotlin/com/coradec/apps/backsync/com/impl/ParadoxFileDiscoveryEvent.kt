package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.util.FileType
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class ParadoxFileDiscoveryEvent(
    origin: Origin,
    file: Path,
    attrs: BasicFileAttributes,
    val type: FileType
): FileDiscoveryEvent(origin, file, attrs)