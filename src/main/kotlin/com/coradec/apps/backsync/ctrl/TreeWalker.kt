package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicTreeWalker
import com.coradec.apps.backsync.model.Recipe
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue

interface TreeWalker {
    val next: Path?
    val post: LinkedBlockingQueue<Path>

    companion object {
        operator fun invoke(root: Path, recipe: Recipe): TreeWalker =
            BasicTreeWalker(root, recipe)
    }
}