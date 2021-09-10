package com.coradec.apps.backsync.model.impl

import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import com.coradec.coradeck.core.util.FileType.Companion.type
import com.coradec.coradeck.core.util.without
import java.nio.file.Path
import java.util.*

data class Exclusions(
    val type: EnumSet<FileType> = EnumSet.noneOf(FileType::class.java),
    val prefix: List<String> = emptyList(),
    val pattern: List<Regex> = emptyList()
) {
    infix fun matches(path: Path): Boolean = path.type.let { filetype ->
        prefix.any { path.toString().startsWith(it) } ||
                filetype in type.without { it !in listOf(REGULAR, DIRECTORY, SYMLINK) }.ifEmpty { EnumSet.of(UNKNOWN) } ||
                pattern.any { it matches path.toString() }
    }

    infix fun matchesPrefix(path: Path): Boolean = prefix.any { path.toString().startsWith(it) }
    infix fun matchesTypeOrPattern(path: Path): Boolean = path.type.let { filetype ->
        filetype in type.without { it !in listOf(REGULAR, DIRECTORY, SYMLINK) }.ifEmpty { EnumSet.of(UNKNOWN) } ||
                pattern.any { it matches path.toString() }
    }
}
