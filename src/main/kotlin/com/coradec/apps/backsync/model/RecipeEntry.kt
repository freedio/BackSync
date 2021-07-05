package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicRecipeEntry

interface RecipeEntry {
    companion object {
        operator fun invoke(repr: String): RecipeEntry = BasicRecipeEntry(repr)
    }
}
