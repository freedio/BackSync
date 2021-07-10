package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileDescriptor
import com.coradec.coradeck.core.util.asLocalDateTime
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

interface FileDescriptor {
    val path: Path
    val changed: LocalDateTime
    val mode: FileAccessMode
    val type: FileType
    val owner: FileOwner
    val size: Long
    val fullpath: String

    companion object {
        operator fun invoke(fname: String, flastmod: String, faccmod: String, fowner: String, fsize: String): FileDescriptor =
            BasicFileDescriptor(
                Paths.get(fname),
                flastmod.take(29).asLocalDateTime("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn"),
                FileAccessMode(faccmod.take(4).toInt(8)),
                FileType(faccmod.drop(4)),
                FileOwner(fowner),
                fsize.toLong()
            )
    }
}
