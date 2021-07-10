package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicRecipe
import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.conf.model.Property

interface Recipe {
    val exclusions: Exclusions

    companion object {
        operator fun invoke(): Recipe = BasicRecipe()
    }
}
