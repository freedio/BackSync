package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.impl.LocalReceiver
import com.coradec.apps.backsync.ctrl.impl.RemoteReceiver

interface FileReceiver {
    /** Request to create the specified regular file with the specified attributes. */
    fun createRegularFile(request: RegularFileDiscovered)
    /** Request to create the specified physical directory with the specified attributes. */
    fun createPhysicalDirectory(request: DirectoryDiscovered)
    /** Request to create the specified symbolic link with the specified attributes. */
    fun createSymbolicLink(request: SymbolicLinkDiscovered)
    /** Request to create the specified loop link with the specified attributes. */
    fun createLoopLink(request: LoopLinkDiscovered)
    /** Request to create the specified lost link with the specified attributes. */
    fun createLostLink(request: LostLinkDiscovered)
    /** Request to update the specified directory with the specified attributes. */
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
