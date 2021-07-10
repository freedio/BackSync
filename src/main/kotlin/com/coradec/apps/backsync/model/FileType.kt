package com.coradec.apps.backsync.model

enum class FileType(val formatted: String) {
    REGULAR("f"),
    DIRECTORY("d"),
    SOCKET("s"),
    BLOCKDEVICE("b"),
    CHARDEVICE("c"),
    PIPE("p"),
    SYMLINK("l"),
    DOOR("D"),
    LOOP_LINK("L"),
    LOST_LINK("N");

    companion object {
        operator fun invoke(type: String): FileType = when (type) {
            "f" -> REGULAR
            "d" -> DIRECTORY
            "s" -> SOCKET
            "b" -> BLOCKDEVICE
            "c" -> CHARDEVICE
            "p" -> PIPE
            "l" -> SYMLINK
            "D" -> DOOR
            "L" -> LOOP_LINK
            "N" -> LOST_LINK
            else -> throw IllegalArgumentException("Unknown file type: ‹$type›!")
        }
    }
}
