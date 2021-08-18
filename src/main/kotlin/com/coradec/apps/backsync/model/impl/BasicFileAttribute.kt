package com.coradec.apps.backsync.model.impl

import java.nio.file.attribute.FileAttribute

class BasicFileAttribute<T>(val name: String, val value: T): FileAttribute<T> {
    override fun name(): String = name
    override fun value(): T = value
}