package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin

class FileDiscoveryEvent(origin: Origin, val descriptor: FileDescriptor) : BasicEvent(origin)
