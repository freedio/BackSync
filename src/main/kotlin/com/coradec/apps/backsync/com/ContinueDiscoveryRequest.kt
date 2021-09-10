package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.ctrl.TreeWalker
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.Request
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.ctrl.module.CoraControl
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ContinueDiscoveryRequest(
    origin: Origin,
    val trigger: Request,
    private val treeWalker: TreeWalker,
    target: Recipient? = null,
    validFrom: ZonedDateTime = ZonedDateTime.now()
) : BasicRequest(origin, target = target) {
    val delayedCopy get() = ContinueDiscoveryRequest(origin, trigger, treeWalker, recipient, validFrom = backlogDelay)
    var count = 0
    val next: Path? get() = treeWalker.next

    override fun copy(recipient: Recipient?) = ContinueDiscoveryRequest(origin, trigger, treeWalker, recipient)

    companion object {
        val IMMEX = CoraControl.IMMEX
        private val backlogDelay get() = ZonedDateTime.now().plus(IMMEX.load.toLong(), ChronoUnit.MILLIS)
    }
}
