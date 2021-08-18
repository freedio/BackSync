package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileOwner
import java.nio.file.attribute.GroupPrincipal
import java.nio.file.attribute.UserPrincipal

class PrincipalFileOwner(override val owner: UserPrincipal? = null, override val group: GroupPrincipal? = null) : FileOwner {
    override fun toString(): String = "%s:%s".format(owner?.name ?: "invalid", group?.name ?: "invalid")
}
