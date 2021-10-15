package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.DiscoveryEndEvent
import com.coradec.apps.backsync.com.FileDiscoveryErrorEvent
import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.impl.BasicFileScanner
import com.coradec.coradeck.com.model.Information
import com.coradec.coradeck.com.model.MultiRequest
import com.coradec.coradeck.com.model.Notification
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.util.FileType.DIRECTORY
import com.coradec.coradeck.core.util.FileType.REGULAR
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.ctrl.module.ignore
import com.coradec.coradeck.ctrl.module.receive
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.model.LocalText
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch

internal class FileScannerTest {

    @Test
    fun testFull() {
        // given
        val root = Paths.get(ROOT)
        val testee = BasicFileScanner(root, Filter())
        val collector = FileScannerResult()
        val expected = mutableListOf(
            "/tmp/FileScannerTest",
            "/tmp/FileScannerTest/first",
            "/tmp/FileScannerTest/second",
            "/tmp/FileScannerTest/first/F2.txt",
            "/tmp/FileScannerTest/second/L1.txt",
            "/tmp/FileScannerTest/second/root",
            "/tmp/FileScannerTest/second/F3.pdf",
            "/tmp/FileScannerTest/first/L2.pdf",
            "/tmp/FileScannerTest/first/F1.txt",
            "/tmp/FileScannerTest/fourth",
            "/tmp/FileScannerTest/third",
            "/tmp/FileScannerTest/third/F4.txt",
            "/tmp/FileScannerTest/third/F5.txt",
            "/tmp/FileScannerTest/third/dir"
        )
        // when
        collector.register()
        testee.execute().standby()
        collector.unregister()
        // then
        assertThat(collector.all.map { it.toString() }).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun testExclusionsByType() {
        // given
        val root = Paths.get(ROOT)
        val testee = BasicFileScanner(root, Filter(REGULAR))
        val collector = FileScannerResult()
        val expected = mutableListOf(
            "/tmp/FileScannerTest",
            "/tmp/FileScannerTest/first",
            "/tmp/FileScannerTest/second",
            "/tmp/FileScannerTest/second/L1.txt",
            "/tmp/FileScannerTest/second/root",
            "/tmp/FileScannerTest/first/L2.pdf",
            "/tmp/FileScannerTest/fourth",
            "/tmp/FileScannerTest/third",
            "/tmp/FileScannerTest/third/dir"
        )
        // when
        collector.register()
        testee.execute()
        collector.unregister()
        // then
        assertThat(collector.all.map { it.toString() }).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun testExclusionsByPrefix() {
        // given
        val root = Paths.get(ROOT)
        val testee = BasicFileScanner(root, Filter("/tmp/FileScannerTest/first", "/tmp/FileScannerTest/fourth"))
        val collector = FileScannerResult()
        val expected = mutableListOf(
            "/tmp/FileScannerTest",
            "/tmp/FileScannerTest/second",
            "/tmp/FileScannerTest/second/L1.txt",
            "/tmp/FileScannerTest/second/root",
            "/tmp/FileScannerTest/second/F3.pdf",
            "/tmp/FileScannerTest/third",
            "/tmp/FileScannerTest/third/F4.txt",
            "/tmp/FileScannerTest/third/F5.txt",
            "/tmp/FileScannerTest/third/dir"
        )
        // when
        collector.register()
        testee.execute()
        collector.unregister()
        // then
        assertThat(collector.all.map { it.toString() }).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun testExclusionsByPrefixWithWildcard() {
        // given
        val root = Paths.get(ROOT)
        val testee = BasicFileScanner(root, Filter("/tmp/FileScannerTest/f*", "/tmp/FileScannerTest/f\\*"))
        val collector = FileScannerResult()
        val expected = mutableListOf(
            "/tmp/FileScannerTest",
            "/tmp/FileScannerTest/second",
            "/tmp/FileScannerTest/second/L1.txt",
            "/tmp/FileScannerTest/second/root",
            "/tmp/FileScannerTest/second/F3.pdf",
            "/tmp/FileScannerTest/third",
            "/tmp/FileScannerTest/third/F4.txt",
            "/tmp/FileScannerTest/third/F5.txt",
            "/tmp/FileScannerTest/third/dir"
        )
        // when
        collector.register()
        testee.execute()
        collector.unregister()
        // then
        assertThat(collector.all.map { it.toString() }).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun testExclusionsByPattern() {
        // given
        val root = Paths.get(ROOT)
        val testee = BasicFileScanner(root, Filter(Regex("\\.pdf$"), Regex("/second/")))
        val collector = FileScannerResult()
        val expected = mutableListOf(
            "/tmp/FileScannerTest",
            "/tmp/FileScannerTest/first",
            "/tmp/FileScannerTest/second",
            "/tmp/FileScannerTest/first/F2.txt",
            "/tmp/FileScannerTest/first/F1.txt",
            "/tmp/FileScannerTest/fourth",
            "/tmp/FileScannerTest/third",
            "/tmp/FileScannerTest/third/F4.txt",
            "/tmp/FileScannerTest/third/F5.txt",
            "/tmp/FileScannerTest/third/dir"
        )
        // when
        collector.register()
        testee.execute()
        collector.unregister()
        // then
        assertThat(collector.all.map { it.toString() }).containsExactlyInAnyOrderElementsOf(expected)
    }

    @Test
    fun testExclusionsByCombination() {
        // given
        val root = Paths.get(ROOT)
        val testee = BasicFileScanner(
            root, Filter
                .byType(DIRECTORY)
                .byPrefix("/tmp/FileScannerTest/third")
                .byPattern(Regex("\\.pdf$"))
        )
        val collector = FileScannerResult()
        val expected = mutableListOf(
            "/tmp/FileScannerTest/first/F2.txt",
            "/tmp/FileScannerTest/second/L1.txt",
            "/tmp/FileScannerTest/second/root",
            "/tmp/FileScannerTest/first/F1.txt"
        )
        // when
        collector.register()
        testee.execute()
        collector.unregister()
        // then
        assertThat(collector.all.map { it.toString() }).containsExactlyInAnyOrderElementsOf(expected)
    }

    class FileScannerResult : BasicAgent() {
        private val files = arrayListOf<Path>()
        private val dirs = arrayListOf<Path>()
        val all = arrayListOf<Path>()
        val latch = CountDownLatch(1)

        fun register() {
            receive(Information::class)
        }

        fun unregister() {
            latch.await()
            ignore()
        }

        override fun receive(notification: Notification<*>) =
            when (val message = notification.content) {
                is DirectoryDiscovered -> {
                    dirs.add(message.path)
                    all.add(message.path)
                    message.succeed()
                }
                is RegularFileDiscovered -> {
                    files.add(message.path)
                    all.add(message.path)
                    message.succeed()
                }
                is SymbolicLinkDiscovered -> {
                    files.add(message.path)
                    all.add(message.path)
                    message.succeed()
                }
                is LoopLinkDiscovered -> {
                    files.add(message.path)
                    all.add(message.path)
                    message.succeed()
                }
                is LostLinkDiscovered -> {
                    files.add(message.path)
                    all.add(message.path)
                    message.succeed()
                }
                is FileDiscoveryErrorEvent -> {
                    if (message.problem is AccessDeniedException) detail("File «%s» is not accessible.", message.file)
                    else error(message.problem, TEXT_ERROR_OCCURRED)
                }
                is DiscoveryEndEvent -> latch.countDown()
                is DirectoryUpdate -> message.succeed()
                is MultiRequest -> message.execute()
                else -> super.receive(notification)
            }
    }

    companion object {
        init {
            CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
        }

        private val TEXT_ERROR_OCCURRED = LocalText("ErrorOccurred")

        private val ROOT = "/tmp/FileScannerTest"

        @BeforeAll
        @JvmStatic
        fun setup() {
            com.coradec.coradeck.core.util.Files.deleteTree(ROOT.toPath())
            Files.createDirectories("/tmp/FileScannerTest".toPath())
            Files.createDirectory("/tmp/FileScannerTest/first".toPath())
            Files.createDirectory("/tmp/FileScannerTest/second".toPath())
            Files.createDirectory("/tmp/FileScannerTest/third".toPath())
            Files.createDirectory("/tmp/FileScannerTest/fourth".toPath())
            Files.createFile("/tmp/FileScannerTest/first/F1.txt".toPath())
            Files.createFile("/tmp/FileScannerTest/first/F2.txt".toPath())
            Files.createFile("/tmp/FileScannerTest/second/F3.pdf".toPath())
            Files.createFile("/tmp/FileScannerTest/third/F4.txt".toPath())
            Files.createFile("/tmp/FileScannerTest/third/F5.txt".toPath())
            Files.createSymbolicLink("/tmp/FileScannerTest/second/L1.txt".toPath(), "/tmp/FileScannerTest/third/F5.txt".toPath())
            Files.createSymbolicLink("/tmp/FileScannerTest/first/L2.pdf".toPath(), "/tmp/FileScannerTest/fourth/F6.pdf".toPath())
            Files.createSymbolicLink("/tmp/FileScannerTest/third/dir".toPath(), "/tmp/FileScannerTest/third".toPath())
            Files.createSymbolicLink("/tmp/FileScannerTest/second/root".toPath(), "/tmp/FileScannerTest".toPath())
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            com.coradec.coradeck.core.util.Files.deleteTree(ROOT.toPath())
        }
    }
}

private fun String.toPath(): Path = Paths.get(this)
