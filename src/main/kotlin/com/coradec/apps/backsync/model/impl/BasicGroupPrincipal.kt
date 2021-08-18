package com.coradec.apps.backsync.model.impl

import java.nio.file.attribute.GroupPrincipal

class BasicGroupPrincipal(val group: String) : GroupPrincipal {
    override fun getName(): String = group
}
