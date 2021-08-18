package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.util.*
import kotlin.random.Random

internal class ExclusionsTest {

    @Test
    fun testNonMatch() {
        // given:
        val t1 = Exclusions(prefix = listOf("/home"))
        val t2 = Exclusions(type = EnumSet.of(BLOCKDEVICE))
        val t3 = Exclusions(pattern = listOf(Regex(".*\\.tmp")))
        val t4 = Exclusions(prefix = listOf("/home"), pattern = listOf(Regex(".*\\.tmp")))
        // when:
        val r1 = t1.matches(FileDescriptor.invoke(Paths.get("/etc/passwd"), TestFileAttributes()))
        val r2 = t2.matches(FileDescriptor.invoke(Paths.get("/etc/passwd"), TestFileAttributes()))
        val r3 = t3.matches(FileDescriptor.invoke(Paths.get("/etc/passwd"), TestFileAttributes()))
        val r4 = t4.matches(FileDescriptor.invoke(Paths.get("/etc/passwd"), TestFileAttributes()))
        // then:
        assertThat(r1).isFalse()
        assertThat(r2).isFalse()
        assertThat(r3).isFalse()
        assertThat(r4).isFalse()
    }

    @Test
    fun testMatch() {
        // given:
        val t1 = Exclusions(prefix = listOf("/home"))
        val t2 = Exclusions(type = EnumSet.of(PIPE))
        val t3 = Exclusions(pattern = listOf(Regex(".*\\.tmp")))
        val t4 = Exclusions(prefix = listOf("/home"), pattern = listOf(Regex(".*\\.tmp")))
        // when:
        val r1 = t1.matches(FileDescriptor.invoke(Paths.get("/home/fred/file.tmp"), TestFileAttributes(PIPE)))
        val r2 = t2.matches(FileDescriptor.invoke(Paths.get("/home/fred/file.tmp"), TestFileAttributes(PIPE)))
        val r3 = t3.matches(FileDescriptor.invoke(Paths.get("/home/fred/file.tmp"), TestFileAttributes(PIPE)))
        val r4 = t4.matches(FileDescriptor.invoke(Paths.get("/home/fred/file.tmp"), TestFileAttributes(PIPE)))
        // then:
        assertThat(r1).isTrue()
        assertThat(r2).isTrue()
        assertThat(r3).isTrue()
        assertThat(r4).isTrue()
    }

    class TestFileAttributes(vararg types: FileType) : BasicFileAttributes {
        private val type: EnumSet<FileType> = EnumSet.copyOf(setOf(*types).ifEmpty { setOf(REGULAR) })
        override fun lastAccessTime(): FileTime = FileTime.fromMillis(0)
        override fun lastModifiedTime(): FileTime = FileTime.from(Instant.now().minusSeconds(10_000_000))
        override fun creationTime(): FileTime = FileTime.from(Instant.now())
        override fun isRegularFile(): Boolean = REGULAR in type
        override fun isDirectory(): Boolean = DIRECTORY in type
        override fun isSymbolicLink(): Boolean = SYMLINK in type
        override fun isOther(): Boolean = type.all { it !in EnumSet.of(REGULAR, DIRECTORY, SYMLINK) }
        override fun size(): Long = Random.nextLong()
        override fun fileKey(): Any = UUID.randomUUID()
    }

}