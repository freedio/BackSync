package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.model.Priority

open class DiscoveryEvent(origin: Origin, priority: Priority): BasicEvent(origin, priority)
