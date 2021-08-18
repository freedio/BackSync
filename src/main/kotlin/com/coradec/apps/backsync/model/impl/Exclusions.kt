package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import com.coradec.coradeck.core.util.without
import java.util.*

data class Exclusions(
    val type: EnumSet<FileType> = EnumSet.noneOf(FileType::class.java),
    val prefix: List<String> = emptyList(),
    val pattern: List<Regex> = emptyList()
) {
    infix fun matches(descr: FileDescriptor): Boolean = prefix.any { descr.path.toString().startsWith(it) } ||
                descr.type in type.without { it !in listOf(REGULAR, DIRECTORY, SYMLINK) }.ifEmpty { EnumSet.of(UNKNOWN) } ||
                pattern.any { it.matches(descr.path.toString()) }
}
