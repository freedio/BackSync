package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.FileWriter
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.impl.BasicFileAttribute
import com.coradec.apps.backsync.trouble.InvalidFileTypeException
import com.coradec.coradeck.core.util.FileType.*
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.text.model.LocalText
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.*
import java.nio.file.attribute.*
import java.time.ZoneOffset
import java.util.concurrent.LinkedBlockingQueue

class BasicFileWriter(val pool: LinkedBlockingQueue<FileWriter>) : BasicAgent(), FileWriter {
    override fun sendTo(sourceDescriptor: FileDescriptor, targetPath: Path) {
        val tpath = targetPath
        val ftime = FileTime.from(sourceDescriptor.changed.toInstant(ZoneOffset.UTC))
        val tattr = setOf<FileAttribute<*>>(
//            BasicFileAttribute("lastModifiedTime", ftime),
//            BasicFileAttribute("lastAccessTime", ftime),
//            BasicFileAttribute("creationTime", ftime),
//            BasicFileAttribute("size", td.size),
//            BasicFileAttribute("isRegularFile", td.type == REGULAR),
//            BasicFileAttribute("isDirectory", td.type == DIRECTORY),
//            BasicFileAttribute("isSymbolicLink", td.type == SYMLINK),
//            BasicFileAttribute("isOther", td.type !in setOf(REGULAR, DIRECTORY, SYMLINK)),
//            BasicFileAttribute("owner", td.owner.owner),
//            BasicFileAttribute("group", td.owner.group),
            BasicFileAttribute("posix:permissions", sourceDescriptor.mode.permissions),
        ).toTypedArray()
        if (!Files.exists(tpath)) {
            val realPath = when (val type = sourceDescriptor.type) {
                REGULAR -> Files.copy(sourceDescriptor.path, tpath, REPLACE_EXISTING)
                DIRECTORY -> Files.createDirectory(tpath, *tattr)
                SYMLINK -> Files.createSymbolicLink(tpath, tpath.toRealPath(), *tattr)
                else -> throw InvalidFileTypeException(TEXT_CANNOT_CREATE[type.name])
            }
            Files.setLastModifiedTime(realPath, ftime)
            sourceDescriptor.owner.owner?.let { Files.setOwner(realPath, it) }
            val posixFile = Files.getFileAttributeView(realPath, PosixFileAttributeView::class.java)
            if (posixFile != null) {
                sourceDescriptor.owner.group?.let { posixFile.setGroup(it) }
                sourceDescriptor.mode.permissions.let { if (it.isNotEmpty()) posixFile.setPermissions(it) }
                posixFile.setTimes(ftime, ftime, ftime)
            }
        }
    }

    override fun close() {
        pool.offer(this)
    }

    companion object {
        val TEXT_CANNOT_CREATE = LocalText("CannotCreate1")
    }
}
