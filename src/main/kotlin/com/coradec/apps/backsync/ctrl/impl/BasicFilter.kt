package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.Filter
import com.coradec.apps.backsync.ctrl.fileTypeOf
import com.coradec.coradeck.core.util.FileType
import java.nio.file.Path

open class BasicFilter(
    val types: Set<FileType> = mutableSetOf(),
    val prefices: Set<String> = mutableSetOf(),
    val patterns: Set<Regex> = mutableSetOf()
) : Filter {
    private val prefixPatterns get() = prefices.map { prefix -> prefix.glob2pattern() }

    fun byType(vararg type: FileType) = BasicFilter(types + type, prefices, patterns)
    fun byPrefix(vararg prefix: String) = BasicFilter(types, prefices + prefix, patterns)
    fun byPattern(vararg pattern: Regex) = BasicFilter(types, prefices, patterns + pattern)

    override fun excludes(path: Path): Boolean = with(path.toString()) {
        fileTypeOf(path) in types || prefixPatterns.any { contains(it) } || patterns.any { this.contains(it) }
    }

    override fun excludesSubtree(path: Path): Boolean = with(path.toString()) {
        prefixPatterns.any { contains(it) } || patterns.any { contains(it) }
    }

    private fun String.glob2pattern(): Regex {
        val p1 = replace(Regex("[^\\\\](\\?)"), ".")
        val p2 = p1.replace(Regex("[^\\\\](\\*)"), ".*")
        return "^$p2".toRegex()
    }
}
