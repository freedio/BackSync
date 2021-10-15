package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.Filter
import com.coradec.apps.backsync.ctrl.fileTypeOf
import com.coradec.coradeck.core.util.FileType
import java.nio.file.Path

class FileTypeFilter(private val types: List<FileType>) : Filter {
    override fun excludes(path: Path): Boolean = fileTypeOf(path) in types
    override fun excludesSubtree(path: Path): Boolean = types.toSet() == FileType.values().toSet()
}
