package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicFileWriter
import com.coradec.coradeck.ctrl.ctrl.Agent

interface SyncWriter : Agent {
    companion object {
        operator fun invoke(): SyncWriter = BasicFileWriter()
    }
}
