package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.com.impl.GenericFileDiscoveryEvent
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.com.model.Event
import com.coradec.coradeck.core.model.StackFrame

interface FileDiscoveryEvent: Event {
    val descriptor: FileDescriptor

    companion object {
        operator fun invoke(here: StackFrame, fileDescriptor: FileDescriptor): FileDiscoveryEvent =
            GenericFileDiscoveryEvent(here, fileDescriptor)
    }
}
