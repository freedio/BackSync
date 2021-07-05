package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.RecipeEntry
import com.coradec.coradeck.core.model.impl.NullIterator
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path

class EmptyRecipe : Recipe {
    override val stream: Sequence<RecipeEntry> get() = Sequence { NullIterator() }
    override fun saveTo(path: Path) {
        Files.createFile(path)
    }
}
