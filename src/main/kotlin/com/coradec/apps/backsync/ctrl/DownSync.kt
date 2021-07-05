package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.Request
import com.coradec.coradeck.com.model.impl.BasicCommand
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.ctrl.model.RequestSet

object DownSync: BackSyncAction() {
    init {
        approve(DownSyncCommand::class)
    }

    fun execute(): Request =
        inject(RequestSet(here, this, DownSyncCommand(here, this)))

    class DownSyncCommand(origin: Origin, recipient: Recipient) : BasicCommand(origin, recipient) {
        override fun execute() {
            TODO("Not yet implemented")
        }
    }
}
