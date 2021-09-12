package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.BackServer
import com.coradec.apps.backsync.ctrl.FileWriter
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.model.AgentPool

class BasicBackClient(recipient: Recipient) : BasicAgent(), BackServer {
    override val writerPool = AgentPool(0, 0) { FileWriter() }

    override fun close() {
        TODO("Not yet implemented")
    }

}
