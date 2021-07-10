package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileAccessMode
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.FileOwner
import com.coradec.apps.backsync.model.FileType
import com.coradec.apps.backsync.model.FileType.*
import java.nio.file.Path
import java.time.LocalDateTime

data class BasicFileDescriptor(
    override val path: Path,
    override val changed: LocalDateTime,
    override val mode: FileAccessMode,
    override val type: FileType,
    override val owner: FileOwner,
    override val size: Long
): FileDescriptor {
    override val fullpath: String = "$path${if (type == DIRECTORY) "/" else ""}"
}