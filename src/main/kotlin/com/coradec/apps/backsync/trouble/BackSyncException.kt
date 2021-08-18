package com.coradec.apps.backsync.trouble

import com.coradec.coradeck.core.trouble.BasicException

open class BackSyncException(message: String?, problem: Throwable?): BasicException(message, problem) {
    constructor(message: String) : this(message, null)

}
