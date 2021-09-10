package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class FileDiscoveryEntryEvent(origin: Origin, val path: Path) : BasicEvent(origin), FileDiscoveryEvent