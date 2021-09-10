package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicFileReader
import com.coradec.coradeck.ctrl.ctrl.Agent

interface FileReader: Agent {
    /** Stops the file reader for the specified reason. */
    fun stop(reason: Throwable)

    companion object {
        operator fun invoke(): FileReader =
            BasicFileReader()
    }
}