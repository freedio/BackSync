package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.BackServer
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent

class BasicBackClient(recipient: Recipient) : BasicAgent(), BackServer {
    override fun close() {
        TODO("Not yet implemented")
    }

}
