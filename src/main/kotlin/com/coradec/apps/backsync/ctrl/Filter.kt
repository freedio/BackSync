package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.*
import com.coradec.coradeck.core.util.FileType
import java.nio.file.Path

interface Filter {
    fun excludes(path: Path): Boolean
    fun excludesSubtree(path: Path): Boolean

    companion object {
        operator fun invoke(): Filter = DummyFilter()
        operator fun invoke(vararg type: FileType): Filter = FileTypeFilter(type.toList())
        operator fun invoke(vararg prefix: String): Filter = PrefixFilter(prefix.toList())
        operator fun invoke(vararg pattern: Regex): Filter = PatternFilter(pattern.toList())
        operator fun invoke(type: Set<FileType>, prefix: Set<String>, pattern: Set<Regex>) = BasicFilter(type, prefix, pattern)
        fun byType(vararg fileType: FileType) = BasicFilter(types = fileType.toMutableSet())
        fun byPrefix(vararg prefix: String) = BasicFilter(prefices = prefix.toMutableSet())
        fun byPattern(vararg pattern: Regex) = BasicFilter(patterns = pattern.toMutableSet())
    }
}