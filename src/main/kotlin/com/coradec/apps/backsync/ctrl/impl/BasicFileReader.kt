package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.ContinueDiscoveryRequest
import com.coradec.apps.backsync.com.StartDiscoveryRequest
import com.coradec.apps.backsync.com.StartDiscoveryVoucher
import com.coradec.apps.backsync.com.impl.FileDiscoveryEndEvent
import com.coradec.apps.backsync.com.impl.FileDiscoveryEntryEvent
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
            debug("Received StartDiscoveryRequest")
            inject(ContinueDiscoveryRequest(here, message, TreeWalker(message.root, message.recipe))).swallow()
        }
        is StartDiscoveryVoucher -> {
            debug("Received StartDiscoveryVoucher")
            inject(ContinueDiscoveryRequest(here, message, TreeWalker(message.root, message.recipe))).swallow()
        }
        is ContinueDiscoveryRequest -> execute {
            debug("Received ContinueDiscoveryRequest")
            val next = message.next
            if (next == null) {
                message.trigger.succeed()
                IMMEX.inject(FileDiscoveryEndEvent(here))
            } else {
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