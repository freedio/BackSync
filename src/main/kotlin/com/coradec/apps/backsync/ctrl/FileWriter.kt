package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicFileWriter
import com.coradec.coradeck.ctrl.ctrl.Agent

interface FileWriter : Agent {
    companion object {
        operator fun invoke(): FileWriter = BasicFileWriter()
    }
}
