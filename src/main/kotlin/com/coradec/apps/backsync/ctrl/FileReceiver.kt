package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.impl.LocalReceiver
import com.coradec.apps.backsync.ctrl.impl.RemoteReceiver
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

interface FileReceiver {
    /** Request to create the specified regular file with the specified attributes. */
    fun createRegularFile(file: Path, attrs: BasicFileAttributes)
    fun createRegularFile(request: RegularFileDiscovered)
    /** Request to create the specified physical directory with the specified attributes. */
    fun createPhysicalDirectory(dir: Path)
    fun createPhysicalDirectory(request: DirectoryDiscovered)
    /** Request to create the specified symbolic link with the specified attributes. */
    fun createSymbolicLink(file: Path, attrs: BasicFileAttributes)
    fun createSymbolicLink(request: SymbolicLinkDiscovered)
    /** Request to create the specified directory symlink with the specified attributes. */
    fun createSymbolicDirectory(dir: Path, attrs: BasicFileAttributes)
    /** Request to create the specified loop link with the specified attributes. */
    fun createLoopLink(file: Path, attrs: BasicFileAttributes)
    fun createLoopLink(request: LoopLinkDiscovered)
    /** Request to create the specified lost link with the specified attributes. */
    fun createLostLink(file: Path, attrs: BasicFileAttributes)
    fun createLostLink(request: LostLinkDiscovered)
    /** Request to update the specified directory with the specified attributes. */
    fun updateDirectory(dir: Path, attrs: BasicFileAttributes)
    fun updateDirectory(request: DirectoryUpdate)
    /** Request to close the file receiver (signal that no more requests will be sent to this instance). */
    fun close()

    companion object {
        operator fun invoke(system: BackendSystem, hostname: String): FileReceiver = when(system) {
            BackendSystem.Local -> LocalReceiver(hostname)
            BackendSystem.Remote -> RemoteReceiver(hostname)
        }
    }
}
