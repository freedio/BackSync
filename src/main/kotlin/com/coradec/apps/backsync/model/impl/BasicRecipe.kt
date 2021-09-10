package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.UpsyncType
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConf
import java.io.BufferedWriter
import java.nio.file.Path

class BasicRecipe(override val exclusions: Exclusions = PROP_EXCLUSIONS.value): Recipe {

    override fun upsyncType(hostname: String, group: String, path: Path): UpsyncType {
        TODO("Not yet implemented")
    }

    override fun writeTo(out: BufferedWriter) {
        CoraConf.yamlMapper.writerWithDefaultPrettyPrinter().writeValue(out, this)
    }

    companion object {
        private val PROP_EXCLUSIONS = LocalProperty<Exclusions>("Exclude")
    }
}
