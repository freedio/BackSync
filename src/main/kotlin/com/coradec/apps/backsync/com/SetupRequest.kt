package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.ctrl.BackServer
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class SetupRequest(origin: Origin, val hostname: String, val group: String, val baseDir: Path):
    BasicRequest(origin)
