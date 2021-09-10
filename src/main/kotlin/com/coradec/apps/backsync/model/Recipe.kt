package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicRecipe
import com.coradec.apps.backsync.model.impl.Exclusions
import java.io.BufferedWriter
import java.nio.file.Path

interface Recipe {
    val exclusions: Exclusions

    /** Returns the upsync type of the file with the specified descriptor for the specified host in the specified group. */
    fun upsyncType(hostname: String, group: String, path: Path): UpsyncType
    /** Writes the recipe to the specified buffered writer. */
    fun writeTo(out: BufferedWriter)

    companion object {
        operator fun invoke(): Recipe = BasicRecipe()
        operator fun invoke(exclusions: Exclusions): Recipe = BasicRecipe(exclusions)
    }
}
