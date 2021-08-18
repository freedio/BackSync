package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileAccessMode
import com.coradec.apps.backsync.model.impl.PosixFileAccessMode
import java.nio.file.attribute.PosixFilePermission

interface FileAccessMode {
    val numeric: Int
    val permissions: Set<PosixFilePermission>

    companion object {
        operator fun invoke(numeric: Int): FileAccessMode = BasicFileAccessMode(numeric)
        operator fun invoke(permissions: Set<PosixFilePermission>?): FileAccessMode =
            PosixFileAccessMode(permissions ?: emptySet())
    }
}
