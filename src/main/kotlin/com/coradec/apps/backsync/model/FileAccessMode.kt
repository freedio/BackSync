package com.coradec.apps.backsync.model

import com.coradec.apps.backsync.model.impl.BasicFileAccessMode

interface FileAccessMode {
    val numeric: Int

    companion object {
        operator fun invoke(numeric: Int): FileAccessMode = BasicFileAccessMode(numeric)
    }
}
