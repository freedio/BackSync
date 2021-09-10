package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class WriteFileRequest(
    origin: Origin,
    val sourcePath: Path,
    val targetPath: Path,
    val targetRealPath: Path?,
    target: Recipient? = null
): BasicRequest(origin, target = target) {
    override fun copy(recipient: Recipient?) = WriteFileRequest(origin, sourcePath, targetPath, targetRealPath, recipient)
}
