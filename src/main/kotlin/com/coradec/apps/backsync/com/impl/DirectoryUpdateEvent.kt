package com.coradec.apps.backsync.com.impl

import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B3
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class DirectoryUpdateEvent(origin: Origin, val dir: Path, val attrs: BasicFileAttributes): BasicEvent(origin, B3)