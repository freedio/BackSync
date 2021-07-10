package com.coradec.apps.backsync.ctrl

import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import java.nio.file.Path

open class Main: BasicAgent() {
    val rootDirectory = LocalProperty<Path>("Root")
}
