package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileAccessMode
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.FileOwner
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.*
import java.time.LocalDateTime
import java.time.ZoneId


class BasicFileDescriptor(override val path: Path, private val attrs: BasicFileAttributes) : FileDescriptor {
    private val posixFileAttributes
        get() = try {
            Files.getFileAttributeView(path, PosixFileAttributeView::class.java).readAttributes()
        } catch (e: Exception) {
            null
        }
    override val changed: LocalDateTime
        get() = LocalDateTime.ofInstant(
            attrs.lastModifiedTime().toInstant(),
            ZoneId.systemDefault()
        )
    override val mode: FileAccessMode get() = FileAccessMode(posixFileAttributes?.permissions())
    override val type: FileType
        get() = when {
            attrs.isRegularFile -> REGULAR
            attrs.isDirectory -> DIRECTORY
            attrs.isSymbolicLink -> SYMLINK // TODO could be more specific
            attrs.isOther -> UNKNOWN // TODO could be more specific
            else -> UNKNOWN
        }
    override val owner: FileOwner get() = FileOwner(posixFileAttributes?.owner(), posixFileAttributes?.group())
    override val size: Long get() = attrs.size()

    override fun copy(path: Path): FileDescriptor = BasicFileDescriptor(path, attrs)

    override fun toString(): String = "%s%s #%d @%s:%s %04o @%s".format(
        path,
        when (type) {
            REGULAR -> ""
            DIRECTORY -> "/"
            SYMLINK -> "->"
            else -> "?"
        },
        size,
        owner.owner?.name ?: "invalid",
        owner.group?.name ?: "invalid",
        mode.numeric,
        changed
    )
}
