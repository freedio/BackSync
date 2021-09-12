package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.ContinueDiscoveryRequest
import com.coradec.apps.backsync.com.StartDiscoveryRequest
import com.coradec.apps.backsync.com.StartDiscoveryVoucher
import com.coradec.apps.backsync.com.impl.FileDiscoveryEndEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryEntryEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryPostDirectoryEvent
import com.coradec.apps.backsync.ctrl.FileReader
import com.coradec.apps.backsync.ctrl.TreeWalker
import com.coradec.coradeck.com.model.Information
import com.coradec.coradeck.core.util.execute
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.core.util.swallow
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.module.CoraControl.IMMEX

class BasicFileReader : BasicAgent(), FileReader {
    @Volatile
    private var active = true
    private var stopReason: Throwable? = null

    override fun onMessage(message: Information) = when (message) {
        is StartDiscoveryRequest -> {
            trace("Received StartDiscoveryRequest")
            inject(ContinueDiscoveryRequest(here, message, TreeWalker(message.root, message.recipe))).swallow()
        }
        is StartDiscoveryVoucher -> {
            trace("Received StartDiscoveryVoucher")
            inject(ContinueDiscoveryRequest(here, message, TreeWalker(message.root, message.recipe))).swallow()
        }
        is ContinueDiscoveryRequest -> execute {
            val post = message.post
            if (post != null) IMMEX.inject(FileDiscoveryPostDirectoryEvent(here, post))
            val next = message.next
            if (next == null) {
                message.trigger.succeed()
                debug("Sent FileDiscoveryEndEvent.")
                IMMEX.inject(FileDiscoveryEndEvent(here))
            } else {
                debug("Received ContinueDiscoveryRequest (%s)", next)
                ++message.count
                IMMEX.inject(FileDiscoveryEntryEvent(here, next))
                inject(message.delayedCopy)
            }
        }
        else -> super.onMessage(message)
    }

    override fun stop(reason: Throwable) {
        debug("Stopping the file reader.")
        stopReason = reason
        active = false
    }
}