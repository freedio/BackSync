package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin

class UpSyncRequest(
    origin: Origin,
    val hostname: String,
    val group: String,
    val fileDescriptor: FileDescriptor
) : BasicRequest(origin)
