package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.ctrl.TreeWalker
import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.ctrl.impl.Logger
import com.coradec.coradeck.text.model.LocalText
import java.io.PrintWriter
import java.nio.file.*
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.io.path.isDirectory
import kotlin.io.path.isSymbolicLink
import kotlin.streams.toList

class BasicTreeWalker(val root: Path, val recipe: Recipe) : Logger(), TreeWalker {
    private val log = PrintWriter(Files.newOutputStream(Paths.get("/tmp/BasicTreeWalker.log")))
    private val current = Stack<Queue<Path>>().apply { push(LinkedList<Path>().apply { add(root) }) }
    private val dirs = Stack<Path>().apply { push(root) }
    override val next: Path? get() = nextP().also { if (it == null) log.close() else log.println(it) }
    override val post = LinkedBlockingQueue<Path>()

    private fun nextP(): Path? {
        while (true) {
            if (current.isEmpty()) return null
            else when (val value = current.peek().poll()) {
                null -> {
                    try {
                        current.pop()
                        post.put(dirs.pop())
                        continue
                    } catch (e: EmptyStackException) {
                        post.put(root)
                        return null
                    }
                }
                else -> {
                    if (recipe.exclusions matchesPrefix value) continue
                    if (!value.isSymbolicLink() && value.isDirectory(LinkOption.NOFOLLOW_LINKS)) {
                        try {
                            dirs.push(value)
                            val fileList = Files.list(value)
                            current.push(LinkedList(fileList.toList()))
                        } catch (e: AccessDeniedException) {
                            warn(TEXT_ACCESS_TO_DIR_DENIED, value)
                        }
                    }
                    if (recipe.exclusions matchesTypeOrPattern value) continue
                    return value
                }
            }
        }
    }

    companion object {
        val TEXT_ACCESS_TO_DIR_DENIED = LocalText("AccessToDirDenied1")
    }
}
