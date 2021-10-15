package com.coradec.apps.backsync.com.impl

import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class FileTransferRequest(origin: Origin, val file: Path): BasicRequest(origin)
