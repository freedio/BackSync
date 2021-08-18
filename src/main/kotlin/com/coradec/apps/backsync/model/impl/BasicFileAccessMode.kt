package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileAccessMode
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermission.*
import java.util.*

data class BasicFileAccessMode(override val numeric: Int) : FileAccessMode {
    override val permissions: Set<PosixFilePermission>
        get() = EnumSet.noneOf(PosixFilePermission::class.java).apply {
            val v = listOf(
                OTHERS_EXECUTE,
                OTHERS_WRITE,
                OTHERS_READ,
                GROUP_EXECUTE,
                GROUP_WRITE,
                GROUP_READ,
                OWNER_EXECUTE,
                OWNER_WRITE,
                OWNER_READ
            )
            IntRange(0, 8).forEach {
                val p = 1 shl it
                if (numeric and p == p) add(v[it])
            }
        }
}
