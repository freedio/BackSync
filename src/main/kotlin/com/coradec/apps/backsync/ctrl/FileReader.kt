package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicFileReader
import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.com.model.Recipient
import java.io.PrintWriter
import java.nio.file.Path

interface FileReader {
    val root: Path
    val exclusions: Exclusions
    val target: Recipient
    val fileLog: PrintWriter

    /**
     *  Starts the file reader.
     * @return the number of files read (negative if the process was interrupted.
     */
    fun start(): Int
    /** Stops the file reader for the specified reason. */
    fun stop(reason: Throwable)

    companion object {
        operator fun invoke(root: Path, exclusions: Exclusions, target: Recipient, fileLog: PrintWriter): FileReader =
            BasicFileReader(root, exclusions, target, fileLog)
    }
}