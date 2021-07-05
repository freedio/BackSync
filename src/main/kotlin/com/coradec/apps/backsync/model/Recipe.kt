package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicRecipe
import com.coradec.apps.backsync.model.impl.EmptyRecipe
import java.nio.file.Path

interface Recipe {
    val stream: Sequence<RecipeEntry>

    fun saveTo(path: Path)

    companion object {
        operator fun invoke(lines: List<String>) = BasicRecipe(lines.asSequence())
        operator fun invoke(content: String) = BasicRecipe(content.lineSequence())
        operator fun invoke() = EmptyRecipe()
    }
}
