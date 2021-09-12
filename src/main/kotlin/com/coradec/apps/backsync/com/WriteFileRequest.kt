package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.A1
import java.nio.file.Path

class WriteFileRequest(
    origin: Origin,
    val sourcePath: Path,
    val targetPath: Path,
    val targetRealPath: Path?,
    target: Recipient? = null,
    priority: Priority = A1
): BasicRequest(origin, priority, target = target) {
    override fun copy(recipient: Recipient?) = WriteFileRequest(origin, sourcePath, targetPath, targetRealPath, recipient, priority)
}
