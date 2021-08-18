package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileAccessMode
import com.coradec.coradeck.core.util.octal
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermission.*
import java.nio.file.attribute.PosixFilePermissions

class PosixFileAccessMode(override val permissions: Set<PosixFilePermission> = emptySet()) : FileAccessMode {
    override val numeric: Int get() = permissions.sumOf { it.value }

    private val PosixFilePermission.value: Int get() = when(this) {
        OWNER_READ -> 400.octal
        OWNER_WRITE -> 200.octal
        OWNER_EXECUTE -> 100.octal
        GROUP_READ -> 40.octal
        GROUP_WRITE -> 20.octal
        GROUP_EXECUTE -> 10.octal
        OTHERS_READ -> 4
        OTHERS_WRITE -> 2
        OTHERS_EXECUTE -> 1
    }
}
