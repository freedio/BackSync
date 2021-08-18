package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicBackClient
import com.coradec.apps.backsync.ctrl.impl.BasicBackServer
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.core.util.classname
import com.coradec.coradeck.ctrl.ctrl.Agent
import java.nio.file.Path

interface BackServer: Agent {
    fun start()
    fun close()

    companion object {
        private val system get() = LocalProperty<String>("System")

        operator fun invoke(recipient: Recipient, system: String): BackServer = when(system) {
            "Remote" -> BasicBackClient(recipient)
            "Local" -> BasicBackServer()
            else -> throw IllegalArgumentException("Invalid property ${BackServer::class.classname}.System: must be ‹Remote› or ‹Local›")
        }
    }
}
