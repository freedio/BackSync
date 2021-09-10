package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.WriteFileRequest
import com.coradec.apps.backsync.ctrl.FileWriter
import com.coradec.apps.backsync.trouble.InvalidFileTypeException
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.text.model.LocalText
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFileAttributes
import java.nio.file.attribute.PosixFilePermissions
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.getOwner
import kotlin.io.path.readAttributes
import kotlin.io.path.setOwner

class BasicFileWriter : BasicAgent(), FileWriter {
    private val log = PrintWriter(Files.newOutputStream(Paths.get("/tmp/BasicFileWriter.log")))
    init {
        addRoute(WriteFileRequest::class, ::writeFile)
    }

    private fun writeFile(request: WriteFileRequest) {
        try {
            debug("FileWriter: got request to write file ${request.targetPath}")
            sendTo(request.sourcePath, request.targetPath, request.targetRealPath)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    override fun sendTo(sourcePath: Path, targetPath: Path, targetRealPath: Path?) {
        log.println("$targetPath")
        val sourceAttr = sourcePath.readAttributes<BasicFileAttributes>()
        val sourceMode = sourcePath.readAttributes<PosixFileAttributes>()
        val sourcePerm = sourcePath.readAttributes<PosixFileAttributes>()
        val ftime = sourcePath.getLastModifiedTime()
        if (!Files.exists(targetPath)) {
            val realPath = when  {
                sourceAttr.isRegularFile -> Files.copy(sourcePath, targetPath, REPLACE_EXISTING, COPY_ATTRIBUTES)
                sourceAttr.isDirectory -> {
                    val dirAccessPermissions = PosixFilePermissions.asFileAttribute(sourcePerm.permissions())
                    Files.createDirectory(targetPath, dirAccessPermissions)
                }
                sourceAttr.isSymbolicLink -> {
                    if (targetRealPath == null) throw IllegalArgumentException("targetRealPath for a SYMLINK must be set!")
                    Files.createSymbolicLink(targetPath, targetRealPath)
                }
                else -> throw InvalidFileTypeException(TEXT_CANNOT_CREATE.content)
            }
            Files.setLastModifiedTime(realPath, ftime)
            sourcePath.getOwner()?.let { targetPath.setOwner(it) }
            val posixFile = Files.getFileAttributeView(realPath, PosixFileAttributeView::class.java)
            if (posixFile != null) {
                posixFile.setGroup(sourceMode.group())
                posixFile.setPermissions(sourceMode.permissions())
                posixFile.setTimes(ftime, ftime, ftime)
            }
        }
    }

    companion object {
        val TEXT_CANNOT_CREATE = LocalText("CannotCreate1")
    }
}
