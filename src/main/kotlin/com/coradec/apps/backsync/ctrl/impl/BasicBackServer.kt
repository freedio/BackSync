package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.*
import com.coradec.apps.backsync.ctrl.BackServer
import com.coradec.apps.backsync.ctrl.FileWriter
import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.UpsyncType
import com.coradec.coradeck.com.model.impl.BasicMessage
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConf
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.core.util.relativeTo
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.ctrl.model.AgentPool
import com.coradec.coradeck.text.model.LocalText
import java.io.PrintWriter
import java.nio.file.*
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileOwnerAttributeView
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFileAttributes
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.readAttributes
import kotlin.system.exitProcess


class BasicBackServer : BasicAgent(), BackServer {
    private val log = PrintWriter(Files.newOutputStream(Paths.get("/tmp/BasicBackServer.log")))
    private val recipes = ConcurrentHashMap<String, Recipe>()
    private val media: Path = PROP_MEDIA.value
    private var upsyncCrashed = false
    override val writerPool = AgentPool(0, PROP_POOL_SIZE.value) { FileWriter() }

    init {
        addRoute(UpSyncRequest::class.java, ::upsync)
        addRoute(UpSyncDirRequest::class.java, ::createDir)
        addRoute(UpSyncDirUpdateRequest::class.java, ::updateDir)
        addRoute(Finish::class.java) { debug("BackServer finished.")}
        addRoute(SetupRequest::class.java, ::setup)
        addRoute(DownloadRecipeVoucher::class.java, ::downloadRecipe)
    }

    override fun close() {
        inject(Finish(this))
    }

    private fun downloadRecipe(voucher: DownloadRecipeVoucher) {
        val recipe = recipes.computeIfAbsent(voucher.group, ::loadGroupRecipe)
        voucher.value = recipe
        voucher.succeed()
    }

    private fun setup(request: SetupRequest) {
        val hostname = request.hostname
        val group = request.group
        val baseDir = request.baseDir
        if (Files.notExists(baseDir)) throw IllegalStateException("Base directory $baseDir does not exist!")
        if (hostname.isBlank()) throw IllegalStateException("Hostname is blank or null: «$hostname»!")
        if (group.isBlank()) throw IllegalStateException("Group name «$group» is blank or null")
        val groupDir = media.resolve(group)
        if (Files.exists(groupDir)) info(TEXT_GROUPDIR_EXISTS, groupDir) else {
            info(TEXT_CREATING_GROUPDIR, groupDir)
            Files.createDirectories(groupDir)
        }
        val recipe = groupDir.resolve("recipe.yaml")
        if (Files.exists(recipe)) info(TEXT_RECIPE_EXISTS, recipe) else {
            info(TEXT_CREATING_RECIPE, recipe)
            Files.newOutputStream(recipe).bufferedWriter().use { out -> Recipe().writeTo(out) }
        }
        request.succeed()
    }

    private fun upsync(request: UpSyncRequest) {
        val hostname = request.hostname
        val group = request.group
        val path = request.path
        if (upsyncCrashed) {
            warn(TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS)
            debug("Terminating1!")
            Thread.sleep(1000)
            debug("Terminating2!")
            exitProcess(1)
        } else {
            log.println("input ${request.group}:${request.hostname}:${request.path}")
            trace("Received UpSyncRequest for ‹%s:%s›", group, path)
            try {
                trace("Reading group recipe for ‹%s› from %s", group, media.resolve(group).resolve("recipe.yaml"))
                val recipe = recipes.computeIfAbsent(group, ::loadGroupRecipe)
                when (recipe.upsyncType(hostname, group, path)) {
                    UpsyncType.NONE -> debug("→ skip.")
                    UpsyncType.BACKUP -> backup(hostname, group, path)
                    UpsyncType.SYNC -> upsync(hostname, group, path)
                }
                request.succeed()
            } catch (e: Exception) {
                upsyncCrashed = true
                error(e)
                request.fail(e)
            }
        }
    }

    private fun createDir(request: UpSyncDirRequest) {
        val hostname = request.hostname
        val group = request.group
        val path = request.path
        if (upsyncCrashed) warn(TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS) else {
            log.println("creDir ${request.group}:${request.hostname}:${request.path}")
            trace("Received UpSyncDirRequest for ‹%s:%s›.", group, path)
            try {
                trace("Reading group recipe for ‹%s› from %s", group, media.resolve(group).resolve("recipe.yaml"))
                val recipe = recipes.computeIfAbsent(group, ::loadGroupRecipe)
                when (recipe.upsyncType(hostname, group, path)) {
                    UpsyncType.NONE -> debug("→ skip.")
                    UpsyncType.BACKUP -> {
                        trace("→ backup.")
                        backupDir(hostname, group, path)
                    }
                    UpsyncType.SYNC -> {
                        trace("→ sync.")
                        upsyncDir(hostname, group, path)
                    }
                }
                request.succeed()
            } catch (e: Exception) {
                upsyncCrashed = true
                error(e)
                request.fail(e)
            }
        }
    }

    private fun updateDir(request: UpSyncDirUpdateRequest) {
        val hostname = request.hostname
        val group = request.group
        val path = request.path
        if (upsyncCrashed) warn(TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS) else {
            updateDir(hostname, group, path)
        }
    }

    private fun backup(hostname: String, group: String, path: Path) {
        log.println("backup $group:$hostname:$path")
        val targetPath = media.resolve(group).resolve(hostname).resolve(path.relativeTo("/"))
        try {
            val targetRealPath = try {
                val relativeRealPath = path.toRealPath().relativeTo("/")
                media.resolve(group).resolve(hostname).resolve(relativeRealPath)
            } catch (e: FileSystemException) {
                if ("Too many levels of symbolic links or unable to access attributes of symbolic link" in (e.message ?: ""))
                warn(TEXT_LOOPLINK_DETECTED, path)
                path
            }
            val backup = if (targetPath.exists(NOFOLLOW_LINKS)) {
                val sattr = path.readAttributes<BasicFileAttributes>()
                val tattr = targetPath.readAttributes<BasicFileAttributes>()
                tattr.lastModifiedTime() != sattr.lastModifiedTime() || tattr.size() != sattr.size()
            } else true
            if (backup) {
                debug("Backing up ‹%s› to ‹%s›", path, targetPath)
                writerPool.inject(WriteFileRequest(here, path, targetPath, targetRealPath))
            } else debug("Not backing up ‹%s› because it is recent and has the same size.", path)
        } catch (e: FileSystemException) {
            if (e.message == "Too many levels of symbolic links or unable to access attributes of symbolic link")
                warn(e, TEXT_LOOPLINK_DETECTED, path)
            else throw e
        } catch (e: NoSuchFileException) {
            warn(e, TEXT_IGNORING_LOST_LINK, targetPath)
        }
    }

    private fun upsync(hostname: String, group: String, path: Path) {
        log.println("upsync $group:$hostname:$path")
        val targetPath = media.resolve(group).resolve("shared").resolve(path.relativeTo("/"))
        try {
            val targetRealPath = try {
                media.resolve(group).resolve(hostname).resolve(path.toRealPath().relativeTo("/"))
            } catch (e: FileSystemException) {
                if ("Too many levels of symbolic links or unable to access attributes of symbolic link" in (e.message ?: ""))
                    warn(TEXT_LOOPLINK_DETECTED, path)
                path
            }
            val sattr = path.readAttributes<BasicFileAttributes>()
            val tattr = targetPath.readAttributes<BasicFileAttributes>()
            if (tattr.lastModifiedTime() < sattr.lastModifiedTime()) {
                debug("Synching ‹%s› to ‹%s›", path, targetPath)
                writerPool.inject(WriteFileRequest(here, path, targetPath, targetRealPath))
            } else debug("Not syncing ‹%s› because it is younger or recent and has the same size.", path)
        } catch (e: NoSuchFileException) {
            error(TEXT_IGNORING_LOST_LINK, targetPath)
        }
    }

    private fun backupDir(hostname: String, group: String, path: Path) {
        log.println("bakDir $group:$hostname:$path")
        val targetPath = media.resolve(group).resolve(hostname).resolve(path.relativeTo("/"))
        val sourceAttributes = Files.readAttributes(path, PosixFileAttributes::class.java, NOFOLLOW_LINKS)
        val usr = sourceAttributes.owner()
        val grp = sourceAttributes.group()
        val prm = sourceAttributes.permissions()
        if (Files.notExists(targetPath)) {
            debug("Creating backup directory \"%s\"", targetPath)
            val dir = Files.createDirectories(targetPath)
            debug("Created backup directory \"%s\"", dir)
            val ownerView = Files.getFileAttributeView(targetPath, FileOwnerAttributeView::class.java, NOFOLLOW_LINKS)
            val posixView = Files.getFileAttributeView(targetPath, PosixFileAttributeView::class.java, NOFOLLOW_LINKS)
            val ftime = path.getLastModifiedTime()
            ownerView.owner = usr
            posixView.setGroup(grp)
            posixView.setPermissions(prm)
        }
    }

    private fun updateDir(hostname: String, group: String, path: Path) {
        log.println("up_Dir $group:$hostname:$path")
        val targetPath = media.resolve(group).resolve(hostname).resolve(path.relativeTo("/"))
        if (Files.notExists(targetPath)) {
            debug("Updating backup directory \"%s\"", targetPath)
            val posixView = Files.getFileAttributeView(targetPath, PosixFileAttributeView::class.java, NOFOLLOW_LINKS)
            val ftime = path.getLastModifiedTime()
            posixView.setTimes(ftime, ftime, ftime)
        }
    }

    private fun upsyncDir(hostname: String, group: String, path: Path) {
        log.println("upsDir $group:$hostname:$path")
        val targetPath = media.resolve(group).resolve("shared").resolve(path.relativeTo("/"))
        if (Files.notExists(targetPath)) {
            debug("Creating sync directory \"%s\"", targetPath)
            TODO("unimplemented")
            debug("Created sync directory \"%s\"", Files.createDirectories(targetPath))
        }
    }

    private fun loadGroupRecipe(group: String): Recipe =
        CoraConf.yamlMapper.readValue(media.resolve(group).resolve("recipe.yaml").toFile(), com.coradec.apps.backsync.model.impl.ServerRecipe::class.java)

    class Finish(origin: Origin) : BasicMessage(origin)

    companion object {
        private val PROP_POOL_SIZE = LocalProperty<Int>("PoolSize")
        private val PROP_MEDIA = LocalProperty<Path>("Media")
        private val TEXT_GROUPDIR_EXISTS = LocalText("GroupdirExists1")
        private val TEXT_CREATING_GROUPDIR = LocalText("CreatingGroupdir1")
        private val TEXT_RECIPE_EXISTS = LocalText("RecipeExists1")
        private val TEXT_CREATING_RECIPE = LocalText("CreatingRecipe1")
        private val TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS = LocalText("IgnoringFurtherUpsyncRequests")
        private val TEXT_IGNORING_LOST_LINK = LocalText("IgnoringLostLink1")
        private val TEXT_LOOPLINK_DETECTED = LocalText("LoopLinkDetected1")
    }
}
