package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.Filter
import java.nio.file.Path

class PrefixFilter(prefices: List<String>) : Filter {
    private val patterns = prefices.map { prefix -> prefix.glob2pattern() }

    override fun excludes(path: Path): Boolean = with (path.toString()) { patterns.any { contains(it) } }
    override fun excludesSubtree(path: Path): Boolean = excludes(path)

    private fun String.glob2pattern(): Regex {
        val p1 = replace(Regex("([^\\\\])\\?"), "$1.")
        val p2 = p1.replace(Regex("([^\\\\])\\*"), "$1.*")
        return "^$p2".toRegex()
    }
}
