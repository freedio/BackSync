package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.model.BackSyncType
import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import java.net.URI
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

open class BackSyncAction: BasicAgent() {
    val recipePath = LocalProperty<Path>("Recipe")
    val backsyncType = LocalProperty<BackSyncType>("BSType")
    val target = LocalProperty<URI>("Target")
    val exclusions = LocalProperty<Set<Path>>("Exclude")
    val recipe: Recipe get() = try {
        Recipe(Files.readAllLines(recipePath.value))
    } catch (e: NoSuchFileException) {
        Recipe()
    }
}
