package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.DirectoryDiscoveryEvent
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class PhysicalDirectoryDiscoveryEvent(origin: Origin, dir: Path, attrs: BasicFileAttributes):
    DirectoryDiscoveryEvent(origin, dir, attrs)