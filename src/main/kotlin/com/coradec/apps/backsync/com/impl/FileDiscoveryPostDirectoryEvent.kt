package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class FileDiscoveryPostDirectoryEvent(origin: Origin, val directory: Path): BasicEvent(origin), FileDiscoveryEvent