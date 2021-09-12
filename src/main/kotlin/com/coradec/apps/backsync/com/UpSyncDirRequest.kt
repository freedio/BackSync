package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.A2
import java.nio.file.Path

class UpSyncDirRequest(
    origin: Origin,
    val hostname: String,
    val group: String,
    val path: Path,
    priority: Priority = A2,
    target: Recipient? = null
) : BasicRequest(origin, priority, target = target)
