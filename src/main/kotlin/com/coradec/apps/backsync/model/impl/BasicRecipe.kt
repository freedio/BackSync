package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.UpsyncType
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConf
import java.io.BufferedWriter

class BasicRecipe: Recipe {
    override val exclusions get() = PROP_EXCLUSIONS.value

    override fun upsyncType(hostname: String, group: String, fileDescriptor: FileDescriptor): UpsyncType {
        TODO("Not yet implemented")
    }

    override fun writeTo(out: BufferedWriter) {
        CoraConf.yamlMapper.writerWithDefaultPrettyPrinter().writeValue(out, this)
    }

    companion object {
        private val PROP_EXCLUSIONS = LocalProperty<Exclusions>("Exclude")
    }
}
