package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.SyncWriter
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.text.model.LocalText
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFileAttributes
import kotlin.io.path.*

class BasicFileWriter : BasicAgent(), SyncWriter {
    init {
        route(RegularFileDiscovered::class, ::regularFileDiscovered)
        route(DirectoryDiscovered::class, ::directoryDiscovered)
        route(SymbolicLinkDiscovered::class, this::symbolicLinkDiscovered)
        route(LostLinkDiscovered::class, ::lostLinkDiscovered)
        route(LoopLinkDiscovered::class, this::loopLinkDiscovered)
        route(FreakDiscovered::class, ::freakDiscovered)
        route(DirectoryUpdate::class, ::updateDirectory)
    }

    private fun directoryDiscovered(request: DirectoryDiscovered) {
        try {
            val sourcePath = request.path
            val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
            do {
                if (targetPath.exists(NOFOLLOW_LINKS)) {
                    info(TEXT_SAME_DIRECTORY_EXISTS, targetPath)
                    break
                }
                info(TEXT_CREATING_DIRECTORY, targetPath)
                Files.createDirectory(targetPath)
                debug(TEXT_DIRECTORY_CREATED, targetPath)
            } while (false)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun regularFileDiscovered(request: RegularFileDiscovered) {
        try {
            val sourcePath = request.path
            val sourceAttr = sourcePath.fileAttributesView<BasicFileAttributeView>(NOFOLLOW_LINKS).readAttributes()
            val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
            do {
                if (targetPath.exists(NOFOLLOW_LINKS)) {
                    val targetAttr = targetPath.readAttributes<BasicFileAttributes>()
                    val sameTime = targetAttr.lastModifiedTime() == sourceAttr.lastModifiedTime()
                    val sameSize = targetAttr.size() == sourceAttr.size()
                    if (sameTime && sameSize) {
                        info(TEXT_SAME_FILE_EXISTS, sourcePath)
                        break
                    }
                }
                info(TEXT_UPLOADING_FILE, sourcePath, request.baseDir)
                Files.copy(sourcePath, targetPath, COPY_ATTRIBUTES, REPLACE_EXISTING, NOFOLLOW_LINKS)
                debug(TEXT_UPLOADED_FILE, sourcePath, request.baseDir)
            } while (false)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun symbolicLinkDiscovered(request: SymbolicLinkDiscovered) {
        try {
            val sourcePath = request.path
            val sourceLinkTarget = sourcePath.readSymbolicLink()
            val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
            do {
                var textCreatingSymlink = TEXT_CREATING_SYMLINK
                var textSymlinkCreated = TEXT_SYMLINK_CREATED
                if (targetPath.exists(NOFOLLOW_LINKS)) {
                    val targetLinkTarget = targetPath.readSymbolicLink()
                    if (targetLinkTarget == sourceLinkTarget) {
                        info(TEXT_SAME_SYMLINK_EXISTS, sourcePath)
                        break
                    } else {
                        textCreatingSymlink = TEXT_UPDATING_SYMLINK
                        textSymlinkCreated = TEXT_SYMLINK_UPDATED
                        Files.delete(targetPath)
                    }
                }
                var cycles = 50
                while (targetPath.parent.notExists() && --cycles != 0) Thread.sleep(100)
                info(textCreatingSymlink, sourcePath, targetPath)
                Files.createSymbolicLink(targetPath, sourceLinkTarget)
                debug(textSymlinkCreated, targetPath)
            } while (false)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun lostLinkDiscovered(request: LostLinkDiscovered) {
        try {
            val sourcePath = request.path
            val sourceLinkTarget = sourcePath.readSymbolicLink()
            val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
            do {
                var textCreatingSymlink = TEXT_CREATING_LOSTLINK
                var textSymlinkCreated = TEXT_LOSTLINK_CREATED
                if (targetPath.exists(NOFOLLOW_LINKS)) {
                    val targetLinkTarget = targetPath.readSymbolicLink()
                    if (targetLinkTarget == sourceLinkTarget) {
                        info(TEXT_SAME_SYMLINK_EXISTS, sourcePath)
                        break
                    } else {
                        textCreatingSymlink = TEXT_UPDATING_LOSTLINK
                        textSymlinkCreated = TEXT_LOSTLINK_UPDATED
                        Files.delete(targetPath)
                    }
                }
                var cycles = 50
                while (targetPath.parent.notExists() && --cycles != 0) Thread.sleep(100)
                info(textCreatingSymlink, sourcePath, targetPath)
                Files.createSymbolicLink(targetPath, sourceLinkTarget)
                debug(textSymlinkCreated, targetPath)
            } while (false)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun loopLinkDiscovered(request: LoopLinkDiscovered) {
        try {
            val sourcePath = request.path
            val sourceLinkTarget = sourcePath.readSymbolicLink()
            val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
            do {
                var textCreatingSymlink = TEXT_CREATING_LOOPLINK
                var textSymlinkCreated = TEXT_LOOPLINK_CREATED
                if (targetPath.exists(NOFOLLOW_LINKS)) {
                    val targetLinkTarget = targetPath.readSymbolicLink()
                    if (targetLinkTarget == sourceLinkTarget) {
                        info(TEXT_SAME_SYMLINK_EXISTS, sourcePath)
                        break
                    } else {
                        textCreatingSymlink = TEXT_UPDATING_LOOPLINK
                        textSymlinkCreated = TEXT_LOOPLINK_UPDATED
                        Files.delete(targetPath)
                    }
                }
                var cycles = 50
                while (targetPath.parent.notExists() && --cycles != 0) Thread.sleep(100)
                info(textCreatingSymlink, sourcePath, targetPath)
                Files.createSymbolicLink(targetPath, sourceLinkTarget)
                debug(textSymlinkCreated, targetPath)
            } while (false)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun updateDirectory(request: DirectoryUpdate) {
        try {
            val sourcePath = request.path
            val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
            val sourceAttrs = sourcePath.readAttributes<BasicFileAttributes>(NOFOLLOW_LINKS)
            val targetAttrs = targetPath.readAttributes<BasicFileAttributes>(NOFOLLOW_LINKS)
            if (sourceAttrs.lastModifiedTime() == targetAttrs.lastModifiedTime())
                info(TEXT_DIRECTORY_NOT_UPDATED, targetPath)
            else {
                info(TEXT_UPDATING_DIRECTORY, targetPath)
                copyAttributes(sourcePath, targetPath)
                debug(TEXT_DIRECTORY_UPDATED, targetPath)
            }
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun freakDiscovered(request: FreakDiscovered) {
        val sourcePath = request.path
        warn(TEXT_SKIPPING_FREAK, sourcePath)
    }

    private fun createRegularFile(request: CreateRegularFileRequest) {
        val sourcePath = request.file
        val sourceAttr = sourcePath.fileAttributesView<BasicFileAttributeView>(NOFOLLOW_LINKS).readAttributes()
        val targetPath = request.baseDir.resolve(sourcePath.relativeTo(root))
        assertParent(targetPath)
        try {
            do {
                if (targetPath.exists(NOFOLLOW_LINKS)) {
                    val targetAttr = targetPath.readAttributes<BasicFileAttributes>()
                    val sameTime = targetAttr.lastModifiedTime() == sourceAttr.lastModifiedTime()
                    val sameSize = targetAttr.size() == sourceAttr.size()
                    if (sameTime && sameSize) {
                        info(TEXT_SAME_FILE_EXISTS, sourcePath)
                        request.succeed()
                        break
                    }
                }
                var cycles = 50
                while (targetPath.parent.notExists() && --cycles != 0) Thread.sleep(100)
                info(TEXT_UPLOADING_FILE, sourcePath, request.baseDir)
                Files.copy(sourcePath, targetPath, COPY_ATTRIBUTES, REPLACE_EXISTING, NOFOLLOW_LINKS)
                debug(TEXT_UPLOADED_FILE, sourcePath, request.baseDir)
            } while (false)
            request.succeed()
        } catch (e: Exception) {
            error(e)
            request.fail(e)
        }
    }

    private fun assertParent(path: Path) {
        val parent = path.parent
        if (parent.notExists()) {
            assertParent(parent)
            info(TEXT_CREATING_NONEXISTENT_DIRECTORY, path)
            try {
                Files.createDirectory(parent)
                debug(TEXT_NONEXISTENT_DIRECTORY_CREATED, path)
            } catch (e: FileAlreadyExistsException) {
                trace("Apparently, original file creator was faster than ‹assertParent› → ignoring.")
            }
        }
    }

    private fun copyAttributes(sourcePath: Path, targetPath: Path) {
        val sourceOwner = sourcePath.getOwner(NOFOLLOW_LINKS)
        val sourcePosix = try {
            sourcePath.readAttributes<PosixFileAttributes>()
        } catch (e: UnsupportedOperationException) {
            null
        }
        val sourcePerms = sourcePosix?.permissions()
        val sourceGroup = sourcePosix?.group()
        val sourceCreat = sourcePosix?.creationTime()
        val sourceAcces = sourcePosix?.lastAccessTime()
        val sourceModif = sourcePath.getLastModifiedTime()
        val targetPosix = Files.getFileAttributeView(targetPath, PosixFileAttributeView::class.java, NOFOLLOW_LINKS)
        if (sourceCreat != null && sourceAcces != null) targetPosix.setTimes(sourceModif, sourceAcces, sourceCreat)
        else targetPath.setLastModifiedTime(sourceModif)
        if (sourceOwner != null) targetPath.setOwner(sourceOwner)
        if (sourcePerms != null) targetPosix.setPermissions(sourcePerms)
        if (sourceGroup != null) targetPosix.setGroup(sourceGroup)
    }

    companion object {
        private val root: Path = Paths.get("/")
        private val TEXT_SAME_FILE_EXISTS = LocalText("SameFileExists1")
        private val TEXT_SAME_DIRECTORY_EXISTS = LocalText("SameDirectoryExists1")
        private val TEXT_SAME_DIRLINK_EXISTS = LocalText("SameSymbolicDirectoryExists1")
        private val TEXT_SAME_SYMLINK_EXISTS = LocalText("SameSymbolicLinkExists1")
        private val TEXT_SAME_LOOPLINK_EXISTS = LocalText("SameLoopLinkExists1")
        private val TEXT_UPLOADING_FILE = LocalText("UploadingFile2")
        private val TEXT_UPLOADED_FILE = LocalText("UploadedFile2")
        private val TEXT_CREATING_DIRECTORY = LocalText("CreatingDirectory1")
        private val TEXT_DIRECTORY_CREATED = LocalText("DirectoryCreated1")
        private val TEXT_CREATING_DIRLINK = LocalText("CreatingSymbolicDirectory2")
        private val TEXT_DIRLINK_CREATED = LocalText("SymbolicDirectoryCreated1")
        private val TEXT_UPDATING_DIRLINK = LocalText("UpdatingSymbolicDirectory2")
        private val TEXT_DIRLINK_UPDATED = LocalText("SymbolicDirectoryUpdated1")
        private val TEXT_CREATING_SYMLINK = LocalText("CreatingSymbolicLink2")
        private val TEXT_SYMLINK_CREATED = LocalText("SymbolicLinkCreated1")
        private val TEXT_UPDATING_SYMLINK = LocalText("UpdatingSymbolicLink2")
        private val TEXT_SYMLINK_UPDATED = LocalText("SymbolicLinkUpdated1")
        private val TEXT_CREATING_LOOPLINK = LocalText("CreatingLoopLink2")
        private val TEXT_LOOPLINK_CREATED = LocalText("LoopLinkCreated1")
        private val TEXT_UPDATING_LOOPLINK = LocalText("UpdatingLoopLink2")
        private val TEXT_LOOPLINK_UPDATED = LocalText("LoopLinkUpdated1")
        private val TEXT_CREATING_LOSTLINK = LocalText("CreatingLostLink2")
        private val TEXT_LOSTLINK_CREATED = LocalText("LostLinkCreated1")
        private val TEXT_UPDATING_LOSTLINK = LocalText("UpdatingLostLink2")
        private val TEXT_LOSTLINK_UPDATED = LocalText("LostLinkUpdated1")
        private val TEXT_UPDATING_DIRECTORY = LocalText("UpdatingDirectory1")
        private val TEXT_DIRECTORY_UPDATED = LocalText("DirectoryUpdated1")
        private val TEXT_DIRECTORY_NOT_UPDATED = LocalText("DirectoryNotUpdated1")
        private val TEXT_CREATING_NONEXISTENT_DIRECTORY = LocalText("CreatingNonexistent_Directory1")
        private val TEXT_NONEXISTENT_DIRECTORY_CREATED = LocalText("Nonexistent_Directory_Created1")
        private val TEXT_SKIPPING_FREAK = LocalText("SkippingFreak1")
    }
}
