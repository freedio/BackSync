package com.coradec.apps.backsync.ctrl

import com.coradec.coradeck.core.util.caller
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.model.RequestList

/**
 * Main class of the UpSync process.
 */

fun main(vararg args: String) {
    UpSync(args.toList()).run()
}

class UpSync(args: List<String>): Main() {
    fun run() {
        FileReader(root, exclusions, this)
    }
}