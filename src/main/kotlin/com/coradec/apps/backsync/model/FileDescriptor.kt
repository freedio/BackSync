package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileDescriptor
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import java.nio.file.LinkOption
import java.nio.file.LinkOption.*
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.time.LocalDateTime

interface FileDescriptor {
    val path: Path
    val changed: LocalDateTime
    val mode: FileAccessMode
    val type: FileType
    val owner: FileOwner
    val size: Long
    val fullpath: String get() = "${path.toRealPath(NOFOLLOW_LINKS)}${if (type == DIRECTORY) "/" else ""}"

    fun copy(path: Path): FileDescriptor

    companion object {
        operator fun invoke(path: Path, attrs: BasicFileAttributes): FileDescriptor = BasicFileDescriptor(path, attrs)
    }
}
