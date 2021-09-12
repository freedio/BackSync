package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.C3
import java.nio.file.Path

class UpSyncDirUpdateRequest(
    origin: Origin,
    val hostname: String,
    val group: String,
    val path: Path,
    priority: Priority = C3,
    target: Recipient? = null
) : BasicRequest(origin, priority, target = target)
