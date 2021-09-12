package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class UpSyncRequest(
    origin: Origin,
    val hostname: String,
    val group: String,
    val path: Path,
    recipient: Recipient? = null
) : BasicRequest(origin, target = recipient)
