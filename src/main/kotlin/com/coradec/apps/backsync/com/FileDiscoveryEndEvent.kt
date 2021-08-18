package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.Event
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin

class FileDiscoveryEndEvent(origin: Origin): BasicEvent(origin)
