package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.impl.*
import com.coradec.apps.backsync.ctrl.FileReceiver

class RemoteReceiver(hostname: String) : FileReceiver {
    override fun createRegularFile(request: RegularFileDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createPhysicalDirectory(request: DirectoryDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createSymbolicLink(request: SymbolicLinkDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createLoopLink(request: LoopLinkDiscovered) {
        TODO("Not yet implemented")
    }

    override fun createLostLink(request: LostLinkDiscovered) {
        TODO("Not yet implemented")
    }

    override fun updateDirectory(request: DirectoryUpdate) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}
