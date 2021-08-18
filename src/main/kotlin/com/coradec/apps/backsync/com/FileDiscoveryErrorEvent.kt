package com.coradec.apps.backsync.com

import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin

class FileDiscoveryErrorEvent(origin: Origin, val problem: Throwable?): BasicEvent(origin)
