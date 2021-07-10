package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.apps.backsync.ctrl.FileReader
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.Path

class StandardFileReader(
    override val root: Path,
    override val exclusions: Set<Regex>,
    override val target: Recipient
) : BasicAgent(), FileReader {
    private val stdState = CONTINUE

    override fun produce() {
        val process: Process = Runtime.getRuntime()
            .exec("find $root -printf %p\\037%CY-%Cm-%CdT%CT\\037%04m%Y\\037%U:%G\\037%s\\n")
        var count = 0
        process.inputStream.bufferedReader().useLines { lines ->
            lines
                .map { line ->
                    val (fname, flastmod, faccmod, fowner, fsize) = line.split(US)
                    FileDescriptor(fname, flastmod, faccmod, fowner, fsize)
                }
                .filter { descr -> exclusions.none { exclusion -> exclusion.matches(descr.fullpath) }}
                .forEach { descr ->
                    target.inject(FileDiscoveryEvent(here, descr))
                }
        }
        val status = process.waitFor()
        if (status != 0)
            throw IllegalStateException("$status: failed to produce files under «$root»!")
    }

    companion object {
        private val US = Char(31)
    }
}