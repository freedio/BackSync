package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.conf.model.LocalProperty
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path

class BasicRecipe: Recipe {
    private val excludes = LocalProperty<Exclusions>("Exclude")
    override val exclusions get() = excludes.value
}
