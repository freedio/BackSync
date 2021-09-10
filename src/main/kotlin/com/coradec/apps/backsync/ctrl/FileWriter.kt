package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicFileWriter
import java.nio.file.Path

interface FileWriter {
    fun sendTo(sourcePath: Path, targetPath: Path, targetRealPath: Path?)

    companion object {
        operator fun invoke() = BasicFileWriter()
    }
}
