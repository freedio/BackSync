package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.StandardFileReader
import com.coradec.coradeck.com.model.Recipient
import java.nio.file.Path

interface FileReader {
    val root: Path
    val exclusions: Set<Regex>
    val target: Recipient

    fun produce()

    companion object {
        operator fun invoke(root: Path, exclusions: Set<Regex>, target: Recipient): FileReader =
            StandardFileReader(root, exclusions, target)
    }
}