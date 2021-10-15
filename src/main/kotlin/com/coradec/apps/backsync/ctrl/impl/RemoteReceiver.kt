package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.FileReceiver
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class RemoteReceiver(hostname: String) : FileReceiver {
    override fun createRegularFile(file: Path, attrs: BasicFileAttributes) {
        TODO("Not yet implemented")
    }

    override fun createRegularFile(request: RegularFileDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createPhysicalDirectory(dir: Path) {
        TODO("Not yet implemented")
    }

    override fun createPhysicalDirectory(request: DirectoryDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createSymbolicLink(file: Path, attrs: BasicFileAttributes) {
        TODO("Not yet implemented")
    }

    override fun createSymbolicLink(request: SymbolicLinkDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createSymbolicDirectory(dir: Path, attrs: BasicFileAttributes) {
        TODO("Not yet implemented")
    }

    override fun createLoopLink(file: Path, attrs: BasicFileAttributes) {
        TODO("Not yet implemented")
    }

    override fun createLoopLink(request: LoopLinkDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createLostLink(file: Path, attrs: BasicFileAttributes) {
        TODO("Not yet implemented")
    }

    override fun createLostLink(request: LostLinkDiscovered) {
        TODO("Not yet implemented")
    }

    override fun updateDirectory(dir: Path, attrs: BasicFileAttributes) {
        TODO("Not yet implemented")
    }

    override fun updateDirectory(request: DirectoryUpdate) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}
