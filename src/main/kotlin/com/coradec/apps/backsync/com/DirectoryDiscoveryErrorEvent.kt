package com.coradec.apps.backsync.com

import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B3
import java.io.IOException
import java.nio.file.Path

class DirectoryDiscoveryErrorEvent(origin: Origin, val dir: Path, val problem: IOException) : DiscoveryEvent(origin, B3)
