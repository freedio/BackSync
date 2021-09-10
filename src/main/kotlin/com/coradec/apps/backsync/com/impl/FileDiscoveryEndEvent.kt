package com.coradec.apps.backsync.com.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.core.model.Origin

class FileDiscoveryEndEvent(origin: Origin): BasicEvent(origin), FileDiscoveryEvent