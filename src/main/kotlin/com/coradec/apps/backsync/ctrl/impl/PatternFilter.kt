package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.Filter
import java.nio.file.Path

class PatternFilter(private val patterns: List<Regex>) : Filter {
    override fun excludes(path: Path): Boolean = with (path.toString()) { patterns.any { contains(it) } }
    override fun excludesSubtree(path: Path): Boolean = excludes(path)
}
