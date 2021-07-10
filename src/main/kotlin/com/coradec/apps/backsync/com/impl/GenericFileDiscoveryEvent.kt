package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin

class GenericFileDiscoveryEvent(origin: Origin, override val descriptor: FileDescriptor) : BasicEvent(origin), FileDiscoveryEvent
