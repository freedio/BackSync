package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicTreeWalker
import com.coradec.apps.backsync.model.Recipe
import java.nio.file.Path

interface TreeWalker {
    val next: Path?

    companion object {
        operator fun invoke(root: Path, recipe: Recipe): TreeWalker =
            BasicTreeWalker(root, recipe)
    }
}