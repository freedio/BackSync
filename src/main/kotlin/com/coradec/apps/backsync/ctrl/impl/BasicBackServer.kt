package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.com.SetupRequest
import com.coradec.apps.backsync.com.UpSyncDirRequest
import com.coradec.apps.backsync.com.UpSyncRequest
import com.coradec.apps.backsync.ctrl.BackServer
import com.coradec.apps.backsync.ctrl.FileWriter
import com.coradec.apps.backsync.model.FileDescriptor
import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.UpsyncType
import com.coradec.apps.backsync.model.impl.BasicRecipe
import com.coradec.coradeck.com.model.impl.BasicEvent
import com.coradec.coradeck.conf.model.LocalProperty
import com.coradec.coradeck.conf.module.CoraConf
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.trouble.FatalException
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.core.util.relativeTo
import com.coradec.coradeck.ctrl.ctrl.impl.BasicAgent
import com.coradec.coradeck.text.model.LocalText
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId

class BasicBackServer : BasicAgent(), BackServer {
    private val recipes: MutableMap<String, Recipe> = mutableMapOf()
    private val media: Path = PROP_MEDIA.value
    private var upsyncCrashed = false
    private val fileWriter: FileWriter get() = FileWriter.take()

    init {
        addRoute(UpSyncRequest::class.java, ::upsync)
        addRoute(UpSyncDirRequest::class.java, ::createDir)
        addRoute(Finish::class.java) { debug("BackServer finished.")}
        addRoute(SetupRequest::class.java, ::setup)
    }

    override fun start() {}

    override fun close() {
        inject(Finish(here))
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
        val fileDescriptor = request.fileDescriptor
        if (upsyncCrashed) warn(TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS) else {
            debug("Received UpSyncRequest for group $group and descriptor $fileDescriptor")
            try {
                trace("Reading group recipe for ‹$group› from ${media.resolve(group).resolve("recipe.yaml")}")
                val recipe = recipes.computeIfAbsent(group, ::loadGroupRecipe)
                when (recipe.upsyncType(hostname, group, fileDescriptor)) {
                    UpsyncType.NONE -> debug("→ skip.")
                    UpsyncType.BACKUP -> {
                        debug("→ backup.")
                        backup(hostname, group, fileDescriptor)
                    }
                    UpsyncType.SYNC -> {
                        debug("→ sync.")
                        upsync(hostname, group, fileDescriptor)
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

    private fun createDir(request: UpSyncDirRequest) {
        val hostname = request.hostname
        val group = request.group
        val fileDescriptor = request.fileDescriptor
        if (upsyncCrashed) warn(TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS) else {
            debug("Received UpSyncRequest for group $group and descriptor $fileDescriptor")
            try {
                trace("Reading group recipe for ‹$group› from ${media.resolve(group).resolve("recipe.yaml")}")
                val recipe = recipes.computeIfAbsent(group, ::loadGroupRecipe)
                when (recipe.upsyncType(hostname, group, fileDescriptor)) {
                    UpsyncType.NONE -> debug("→ skip.")
                    UpsyncType.BACKUP -> {
                        debug("→ backup.")
                        backupDir(hostname, group, fileDescriptor)
                    }
                    UpsyncType.SYNC -> {
                        debug("→ sync.")
                        upsyncDir(hostname, group, fileDescriptor)
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

    private fun backup(hostname: String, group: String, fileDescriptor: FileDescriptor) {
        val targetPath = media.resolve(group).resolve(hostname).resolve(fileDescriptor.path.relativeTo("/"))
        debug("Sending up ${fileDescriptor.path} to $targetPath")
        fileWriter.use { it.sendTo(fileDescriptor, targetPath) }
    }

    private fun upsync(hostname: String, group: String, fileDescriptor: FileDescriptor) {
        val targetPath = media.resolve(group).resolve("shared").resolve(fileDescriptor.path.relativeTo("/"))
        debug("Synching ${fileDescriptor.path} to $targetPath")
        val attributes = Files.readAttributes(targetPath, BasicFileAttributes::class.java)
        if (LocalDateTime.ofInstant(attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()) < fileDescriptor.changed)
            fileWriter.use { it.sendTo(fileDescriptor, targetPath) }
    }

    private fun backupDir(hostname: String, group: String, fileDescriptor: FileDescriptor) {
        val targetPath = media.resolve(group).resolve(hostname).resolve(fileDescriptor.path.relativeTo("/"))
        if (Files.notExists(targetPath)) {
            debug("Creating backup directory \"%s\"", targetPath)
            debug("Created backup directory \"%s\"", Files.createDirectories(targetPath))
        }
    }

    private fun upsyncDir(hostname: String, group: String, fileDescriptor: FileDescriptor) {
        val targetPath = media.resolve(group).resolve("shared").resolve(fileDescriptor.path.relativeTo("/"))
        if (Files.notExists(targetPath)) {
            debug("Creating sync directory \"%s\"", targetPath)
            debug("Created sync directory \"%s\"", Files.createDirectories(targetPath))
        }
    }

    private fun loadGroupRecipe(group: String): Recipe =
        CoraConf.yamlMapper.readValue(media.resolve(group).resolve("recipe.yaml").toFile(), com.coradec.apps.backsync.model.impl.ServerRecipe::class.java)

    class Finish(origin: Origin) : BasicEvent(origin)

    companion object {
        private val PROP_MEDIA = LocalProperty<Path>("Media")
        private val TEXT_GROUPDIR_EXISTS = LocalText("GroupdirExists1")
        private val TEXT_CREATING_GROUPDIR = LocalText("CreatingGroupdir1")
        private val TEXT_RECIPE_EXISTS = LocalText("RecipeExists1")
        private val TEXT_CREATING_RECIPE = LocalText("CreatingRecipe1")
        private val TEXT_IGNORING_FURTHER_UPSYNC_REQUESTS = LocalText("IgnoringFurtherUpsyncRequests")
    }
}
