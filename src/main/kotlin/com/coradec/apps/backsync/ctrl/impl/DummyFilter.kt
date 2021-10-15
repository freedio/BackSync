package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.Filter
import java.nio.file.Path

class DummyFilter : Filter {
    override fun excludes(path: Path): Boolean = false
    override fun excludesSubtree(path: Path): Boolean = false
}
