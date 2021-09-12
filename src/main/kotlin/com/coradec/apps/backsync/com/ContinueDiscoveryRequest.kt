package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.ctrl.TreeWalker
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.Request
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority
import com.coradec.coradeck.core.model.Priority.C2
import java.nio.file.Path
import java.time.ZonedDateTime

class ContinueDiscoveryRequest(
    origin: Origin,
    val trigger: Request,
    private val treeWalker: TreeWalker,
    target: Recipient? = null,
    validFrom: ZonedDateTime = ZonedDateTime.now(),
    priority: Priority = C2
) : BasicRequest(origin, priority, target = target, validFrom = validFrom) {
    val delayedCopy get() = ContinueDiscoveryRequest(origin, trigger, treeWalker, recipient, validFrom = delayed, priority)
    var count = 0
    val next: Path? get() = treeWalker.next
    val post: Path? get() = treeWalker.post.poll()

    companion object {
        private val delayed get() = ZonedDateTime.now()//.plus(10, ChronoUnit.MILLIS)
    }
}
