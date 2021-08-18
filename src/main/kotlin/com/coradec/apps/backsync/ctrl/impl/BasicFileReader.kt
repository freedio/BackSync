package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.FileDiscoveryEndEvent
import com.coradec.apps.backsync.com.FileDiscoveryErrorEvent
import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.apps.backsync.ctrl.FileReader
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.*
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.atomic.AtomicInteger

class BasicFileReader(
    override val root: Path,
    override val exclusions: Exclusions,
    override val target: Recipient,
    override val fileLog: PrintWriter
) : BasicAgent(), FileReader {
    @Volatile private var active = true
    private var stopReason: Throwable? = null
    override fun start(): Int {
        val count = AtomicInteger(0)
        Files.walkFileTree(root, StandardFileVisitor(count))
        target.inject(FileDiscoveryEndEvent(this))
        return count.get().let { if (stopReason != null) -it else it }
    }

    val continuation: FileVisitResult get() = when {
        !active -> TERMINATE
        stopReason != null -> TERMINATE
        else -> CONTINUE
    }

    inner class StandardFileVisitor(private val count: AtomicInteger) : FileVisitor<Path> {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = continuation.let {
            val fd = FileDescriptor(dir, attrs)
            if (!exclusions.matches(fd)) {
                target.inject(FileDiscoveryEvent(this@BasicFileReader, fd))
                count.incrementAndGet()
                Thread.sleep(1)
                it
            } else SKIP_SUBTREE
        }

        override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult = continuation.also {
            val fd = FileDescriptor(path, attrs)
            if (!exclusions.matches(fd)) {
                target.inject(FileDiscoveryEvent(this@BasicFileReader, fd))
                count.incrementAndGet()
                Thread.sleep(2, 500000)
                fileLog.println(fd)
            }
        }

        override fun visitFileFailed(path: Path, exc: IOException): FileVisitResult = continuation.let {
            target.inject(FileDiscoveryErrorEvent(this@BasicFileReader, exc))
            if (Files.isDirectory(path)) SKIP_SUBTREE else it
        }

        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult = continuation.also {
            if (exc != null) target.inject(FileDiscoveryErrorEvent(this@BasicFileReader, exc))
        }
    }

    override fun stop(reason: Throwable) {
        debug("Stopping the file reader.")
        stopReason = reason
        active = false
    }

    companion object {
        private val US = Char(31)
    }
}