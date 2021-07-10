package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileState
import com.coradec.apps.backsync.model.impl.DefaultFileState
import com.coradec.apps.backsync.model.impl.DefectiveFileState

interface FileState {
    companion object {
        val DEFECTIVE: FileState = DefectiveFileState()
        val DEFAULT: FileState = DefaultFileState()

        operator fun invoke(repr: String): FileState = BasicFileState(repr)
    }
}
