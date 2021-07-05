package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.RecipeEntry
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path

class BasicRecipe(private val lines: Sequence<String>): Recipe {
    override val stream: Sequence<RecipeEntry> get() = lines.map { line -> RecipeEntry(line) }
    override fun saveTo(path: Path) = PrintWriter(Files.newBufferedWriter(path)).use { out -> lines.forEach { out.println(it) } }
}
