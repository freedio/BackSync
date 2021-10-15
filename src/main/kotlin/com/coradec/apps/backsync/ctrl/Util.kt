package com.coradec.apps.backsync.ctrl

import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.*

fun fileTypeOf(file: Path): FileType = when {
    file.isSymbolicLink() -> {
        val target = try {
            file.resolve(file.readSymbolicLink())
        } catch (e: NoSuchFileException) {
            null
        }
        when {
            target == null -> LOST_LINK
            file.startsWith(target) -> LOOP_LINK
            target.notExists() -> LOST_LINK
            target.isDirectory() -> DIR_LINK
            else -> SYMLINK
        }
    }
    file.isDirectory() -> DIRECTORY
    file.isRegularFile() -> REGULAR
    else -> UNKNOWN
}
