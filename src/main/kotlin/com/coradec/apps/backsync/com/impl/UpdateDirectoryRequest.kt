package com.coradec.apps.backsync.com.impl

import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority.B3
import java.nio.file.Path

class UpdateDirectoryRequest(origin: Origin, val dir: Path, val baseDir: Path): BasicRequest(origin, B3)
