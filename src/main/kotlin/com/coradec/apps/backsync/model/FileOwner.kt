package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileOwner
import com.coradec.apps.backsync.model.impl.PrincipalFileOwner
import java.nio.file.attribute.GroupPrincipal
import java.nio.file.attribute.UserPrincipal
import java.security.Principal

interface FileOwner {
    val owner: UserPrincipal?
    val group: GroupPrincipal?

    override fun toString(): String

    companion object {
        operator fun invoke(repr: String): FileOwner = BasicFileOwner(repr)
        operator fun invoke(owner: UserPrincipal?, group: GroupPrincipal?): FileOwner = PrincipalFileOwner(owner, group)
    }
}
