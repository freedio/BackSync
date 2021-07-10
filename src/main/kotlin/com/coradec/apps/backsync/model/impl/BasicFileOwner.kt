package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileOwner
import java.lang.NumberFormatException

class BasicFileOwner(repr: String) : FileOwner {
    val parsed: Pair<Int, Int> = repr.split(':', limit = 2).let {
        if (it.size != 2) throw IllegalArgumentException("Owner representation requires format \"‹numeric user›:‹numeric group›\"!")
        try {
            Pair(it[0].toInt(), it[1].toInt())
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Owner representation requires format \"‹numeric user›:‹numeric group›\"!")
        }
    }
    override val user: Int get() = parsed.first
    override val group: Int get() = parsed.second

    override fun equals(other: Any?): Boolean = other is FileOwner && other.user == user && other.group == group
    override fun hashCode(): Int = (user shl 32) or group
    override fun toString(): String = "$user:$group"
}
