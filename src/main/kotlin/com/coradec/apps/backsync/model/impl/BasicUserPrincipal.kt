package com.coradec.apps.backsync.model.impl

import java.nio.file.attribute.UserPrincipal
import java.security.Principal

class BasicUserPrincipal(val owner: String) : UserPrincipal {
    override fun getName(): String = owner
}
