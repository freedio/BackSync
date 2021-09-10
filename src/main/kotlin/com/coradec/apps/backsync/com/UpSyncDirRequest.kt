package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class UpSyncDirRequest(
    origin: Origin,
    val hostname: String,
    val group: String,
    val path: Path
) : BasicRequest(origin)
