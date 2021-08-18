package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.BackServer
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import java.nio.file.Path

class BasicBackClient(recipient: Recipient) : BasicAgent(), BackServer {
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}
