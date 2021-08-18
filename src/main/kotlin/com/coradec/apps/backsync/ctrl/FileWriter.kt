package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.ctrl.impl.BasicFileWriter
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.conf.model.LocalProperty
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

interface FileWriter : AutoCloseable {
    fun sendTo(sourceDescriptor: FileDescriptor, targetPath: Path)

    companion object {
        val PROP_WRITER_POOL_CAPACITY = LocalProperty<Int>("WriterPoolCapacity")
        val writers = AtomicInteger(0)
        val pool = LinkedBlockingQueue<FileWriter>()

        fun take(): FileWriter = pool.poll() ?: createOrWait()

        private fun createOrWait(): FileWriter =
            if (PROP_WRITER_POOL_CAPACITY.value > writers.getAndIncrement()) BasicFileWriter(pool)
            else pool.take().also { writers.decrementAndGet() }
    }
}
