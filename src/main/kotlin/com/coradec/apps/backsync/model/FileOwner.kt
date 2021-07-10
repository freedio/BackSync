package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileOwner

interface FileOwner {
    val user: Int
    val group: Int

    override fun toString(): String

    companion object {
        operator fun invoke(repr: String): FileOwner = BasicFileOwner(repr)
    }
}
