package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.FileDiscoveryEvent
import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.com.model.Information
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.util.FileType.*
import com.coradec.coradeck.core.util.Files.deleteTree
import com.coradec.coradeck.core.util.relax
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

internal class StandardFileReaderTest {

    private val fileLog = PrintWriter(FileWriter("/dev/null"))

    @Test
    fun testNoExclusions() {
        // given:
        val rootPath = Paths.get("/tmp/backsync/")
        val exclusions = Exclusions()
        val processor = NoExclusionsTestProcessor()
        val testee = BasicFileReader(rootPath, exclusions, processor, fileLog)
        // when:
        val count = testee.start()
        processor.waitFor(count)
        // then:
        assertThat(count).isEqualTo(6)
        assertThat(processor.processed.get()).isEqualTo(count)
    }

    @Test
    fun testExcludePrefix() {
        // given:
        val rootPath = Paths.get("/tmp/backsync/")
        val exclusions = Exclusions(prefix = listOf("$ROOT/dir"))
        val processor = NoExclusionsTestProcessor()
        val testee = BasicFileReader(rootPath, exclusions, processor, fileLog)
        // when:
        val count = testee.start()
        processor.waitFor(count)
        // then:
        assertThat(count).isEqualTo(4)
        assertThat(processor.processed.get()).isEqualTo(count)
    }

    @Test
    fun testExcludePattern() {
        // given:
        val rootPath = Paths.get("/tmp/backsync/")
        val exclusions = Exclusions(pattern = listOf(Regex(".*/x")))
        val processor = NoExclusionsTestProcessor()
        val testee = BasicFileReader(rootPath, exclusions, processor, fileLog)
        // when:
        val count = testee.start()
        processor.waitFor(count)
        // then:
        assertThat(count).isEqualTo(4)
        assertThat(processor.processed.get()).isEqualTo(count)
    }

    @Test
    fun testExcludeSymLinks() {
        // given:
        val rootPath = Paths.get("/tmp/backsync/")
        val exclusions = Exclusions(type = EnumSet.of(SYMLINK, LOST_LINK, LOOP_LINK))
        val processor = NoExclusionsTestProcessor()
        val testee = BasicFileReader(rootPath, exclusions, processor, fileLog)
        // when:
        val count = testee.start()
        processor.waitFor(count)
        // then:
        assertThat(count).isEqualTo(4)
        assertThat(processor.processed.get()).isEqualTo(count)
    }

    @Test
    fun testAmount() { // TODO remove this local test
        // given:
        val rootPath = Paths.get("/home/dio/")
        val exclusions = Exclusions(type = EnumSet.of(SYMLINK, LOST_LINK, LOOP_LINK))
        val processor = NoExclusionsTestProcessor()
        val testee = BasicFileReader(rootPath, exclusions, processor, fileLog)
        // when:
        val count = testee.start()
        processor.waitFor(count)
        // then:
        assertThat(count).isGreaterThan(900_000)
        assertThat(processor.processed.get()).isEqualTo(count)
    }

    class NoExclusionsTestProcessor : BasicAgent() {
        val processed = AtomicInteger(0)
        val togo = Semaphore(0)
        fun waitFor(count: Int) {
            togo.acquire(count)
        }

        override fun onMessage(message: Information): Unit = when (message) {
            is FileDiscoveryEvent -> {
                processed.incrementAndGet()
                togo.release()
            }
            else -> super.onMessage(message)
        }
    }

    @BeforeEach
    fun setup() {
        Files.createDirectories(ROOT)
        Files.createFile(ROOT.resolve("x"))
        Files.createDirectory(ROOT.resolve("dir"))
        Files.createFile(ROOT.resolve("dir/x"))
        Files.createSymbolicLink(ROOT.resolve("symlink"), ROOT.resolve("dir/x"))
        Files.createSymbolicLink(ROOT.resolve("lostlink"), ROOT.resolve("dir/y"))
    }

    @AfterEach
    fun teardown() {
        relax()
        deleteTree(ROOT)
    }

    companion object {
        val ROOT = Paths.get("/tmp/backsync")

        init {
            CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
        }
    }

}