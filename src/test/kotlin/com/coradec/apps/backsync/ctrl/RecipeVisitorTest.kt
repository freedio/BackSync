package com.coradec.apps.backsync.ctrl

import com.coradec.coradeck.com.model.impl.Syslog
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.model.ClassPathResource
import com.coradec.coradeck.core.model.ClassPathResource.Companion
import com.coradec.coradeck.core.util.USER_HOME
import com.coradec.coradeck.core.util.classname
import com.coradec.coradeck.core.util.formatted
import com.coradec.coradeck.core.util.relax
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.model.LocalText
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.*
import java.nio.file.FileVisitResult.*
import java.nio.file.attribute.BasicFileAttributes

internal class RecipeVisitorTest {

    @Test
    fun testVisitor() {
        // given:
        val excluded: Set<Path> = emptySet()
        val recWriter = StringWriter()
        val errWriter = StringWriter()
        PrintWriter(recWriter).use { recPrinter ->
            PrintWriter(errWriter).use { errPrinter ->
                val testee = RecipeVisitor(BASE_DIR, recPrinter, errPrinter, excluded)
                // when:
                Files.walkFileTree(BASE_DIR, testee)
            }
        }
        // then:
        val recipe = recWriter.toString()
        val errors = errWriter.toString()
        val expected = ClassPathResource(this::class, "/testVisitor.txt")
        assertThat(recipe).isEqualTo(expected.content)
        assertThat(errors).isEqualTo("")
    }

    @BeforeEach
    fun buildTestDirectory() {
        Syslog.debug("Building test directory...")
        Files.createDirectories(BASE_DIR)
        createSubdirectories(BASE_DIR, PROP_DIR.value)
        Syslog.debug("Built test directory.")
    }

    @AfterEach
    fun destroyTestDirectory() {
        Syslog.debug("Destroying test directory...")
        destroyDirectories(BASE_DIR)
        Syslog.debug("Destroyed test directory.")
    }

    companion object {
        init {
            CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
        }

        val BASE_DIR: Path = Paths.get(USER_HOME, ".coradec", "apps", "backsync", ".test")
        val PROP_DIR = LocalProperty<Any>("DirectoryStructure")
        val TEXT_CANT_DESTROY = LocalText("CantDestroy2")
        private val TEXT_IGNORE_UNRECOGNIZED_STRUCT = LocalText("IgnoreUnrecognizableStruct2")

        private fun createSubdirectories(root: Path, struct: Any?): Unit = when (struct) {
            null -> relax()
            is Map<*, *> -> struct.forEach { name, substruct ->
                if (name != null) {
                    val base: Path = root.resolve(name.toString())
                    Files.createDirectory(base)
                    createSubdirectories(base, substruct)
                }
            }
            is List<*> -> struct.forEach { entry ->
                when (entry) {
                    null -> relax()
                    is Map<*, *> -> createSubdirectories(root, entry)
                    else -> Files.createFile(root.resolve(entry.toString()))
                }
            }
            else -> Syslog.warn(TEXT_IGNORE_UNRECOGNIZED_STRUCT, root, struct)
        }

        private fun destroyDirectories(path: Path) {
            Files.walkFileTree(BASE_DIR, object : FileVisitor<Path> {
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = CONTINUE

                override fun visitFile(file: Path, attrs: BasicFileAttributes) = CONTINUE.also {
                    when {
                        attrs.isRegularFile -> Files.delete(file)
                        attrs.isSymbolicLink -> Files.delete(file)
                        else -> relax()
                    }
                }

                override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult = CONTINUE.also {
                    Syslog.error(exc, TEXT_CANT_DESTROY, BASE_DIR, file)
                }

                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult = CONTINUE.also {
                    Files.delete(dir)
                }

            })
        }
    }

}