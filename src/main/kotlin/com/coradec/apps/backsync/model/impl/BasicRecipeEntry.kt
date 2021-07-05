package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.RecipeEntry
import com.coradec.apps.backsync.model.RecipeEntryDiffability
import com.coradec.apps.backsync.model.RecipeEntryDiffability.Opaque
import com.coradec.apps.backsync.model.RecipeEntryMode
import com.coradec.apps.backsync.model.RecipeEntryMode.Back

data class BasicRecipeEntry(val repr: String) : RecipeEntry {
    private val pathString: String by lazy { repr.substringBefore(':') }
    private val modeChar: Char by lazy { repr.substringAfter(':').let { if (it.isEmpty()) 'B' else it[0] } }
    private val diffabilityChar: Char by lazy { repr.substringAfter(':').let { if (it.length < 2) 'O' else it[1] } }
    val mode: RecipeEntryMode = RecipeEntryMode.values().singleOrNull { it.name[0] == modeChar } ?: Back
    val diffability: RecipeEntryDiffability = RecipeEntryDiffability.values().singleOrNull { it.name[0] == diffabilityChar } ?: Opaque
}
