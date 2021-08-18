package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.UpsyncType
import com.coradec.coradeck.conf.module.CoraConf
import java.io.BufferedWriter

class ServerRecipe(override val exclusions: Exclusions): Recipe {
    override fun upsyncType(hostname: String, group: String, fileDescriptor: FileDescriptor): UpsyncType = UpsyncType.BACKUP

    override fun writeTo(out: BufferedWriter) {
        CoraConf.yamlMapper.writerWithDefaultPrettyPrinter().writeValue(out, this)
    }
}
