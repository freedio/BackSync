package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class RegularFileDiscoveryEvent(origin: Origin, file: Path, attrs: BasicFileAttributes): FileDiscoveryEvent(origin, file, attrs)