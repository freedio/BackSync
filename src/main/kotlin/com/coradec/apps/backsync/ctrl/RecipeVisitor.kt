package com.coradec.apps.backsync.ctrl

import java.io.IOException
import java.io.PrintWriter
import java.nio.file.AccessDeniedException
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.FileVisitResult.SKIP_SUBTREE
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class RecipeVisitor(
    private val basePath: Path,
    private val recipeWriter: PrintWriter,
    private val errorWriter: PrintWriter,
    private val excludedDirectories: Set<Path>
) : FileVisitor<Path> {
    private val prefix = StringBuilder()
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = when (dir) {
        in excludedDirectories -> SKIP_SUBTREE
        else -> {
            if (prefix.isNotEmpty()) recipeWriter.println("$dir/")
            prefix.append('/')
            CONTINUE
        }
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes) = when {
        attrs.isOther -> CONTINUE
        else -> {
            recipeWriter.println("$file:BO")
            CONTINUE
        }
    }

    override fun visitFileFailed(file: Path, exc: IOException) = (if (Files.isDirectory(file)) SKIP_SUBTREE else CONTINUE).also {
        when (exc) {
            is AccessDeniedException -> recipeWriter.println("$file:EO not accessible")
            else -> {
                errorWriter.println("-> $file")
                exc.printStackTrace(errorWriter)
            }
        }
    }

    override fun postVisitDirectory(dir: Path, exc: IOException?) = CONTINUE.also {
        prefix.setLength(prefix.length - 1)
    }

}
