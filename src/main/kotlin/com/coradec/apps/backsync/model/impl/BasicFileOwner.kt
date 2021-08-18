package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileOwner
import java.lang.NumberFormatException
import java.nio.file.attribute.GroupPrincipal
import java.nio.file.attribute.UserPrincipal

class BasicFileOwner(repr: String) : FileOwner {
    val parsed: Pair<String, String> = repr.split(':', limit = 2).let {
        if (it.size != 2) throw IllegalArgumentException("Owner representation requires format \"‹numeric user›:‹numeric group›\"!")
        try {
            Pair(it[0], it[1])
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Owner representation requires format \"‹numeric user›:‹numeric group›\"!")
        }
    }
    override val owner: UserPrincipal get() = BasicUserPrincipal(parsed.first)
    override val group: GroupPrincipal get() = BasicGroupPrincipal(parsed.second)

    override fun equals(other: Any?): Boolean = other is FileOwner && other.owner == owner && other.group == group
    override fun hashCode(): Int = owner.hashCode() + 2 * group.hashCode()
    override fun toString(): String = "${owner.name}:${group.name}"
}
