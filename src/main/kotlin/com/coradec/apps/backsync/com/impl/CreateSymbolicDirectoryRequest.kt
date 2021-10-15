package com.coradec.apps.backsync.com.impl

import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B0
import java.nio.file.Path

class CreateSymbolicDirectoryRequest(origin: Origin, val dir: Path, val baseDir: Path): BasicRequest(origin, B0)
