package com.coradec.apps.backsync.ctrl

enum class BackendSystem {
    Local, Remote;

    companion object {
        operator fun invoke(name: String) = values().single { it.name == name }
    }
}