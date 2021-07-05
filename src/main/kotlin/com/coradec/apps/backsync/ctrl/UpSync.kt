package com.coradec.apps.backsync.ctrl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.Request
import com.coradec.coradeck.com.model.impl.BasicCommand
import com.coradec.coradeck.conf.model.Property
import com.coradec.coradeck.core.model.Origin
import com.coradec.coradeck.core.util.here
import com.coradec.coradeck.ctrl.model.RequestSet
import com.coradec.coradeck.text.model.LocalText
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object UpSync : BackSyncAction() {
    private val BASE_PATH = "/"
    private val TEXT_PROBLEMS_IN_RECIPE = LocalText("ProblemsInRecipe1")

    init {
        approve(UpdateRecipeCommand::class, UpSyncCommand::class)
    }

    fun execute(): Request = inject(
        RequestSet(
            here, this,
            UpdateRecipeCommand(here, this, recipePath.value, exclusions.value),
            UpSyncCommand(here, this, recipe)
        )
    )

    class UpSyncCommand(origin: Origin, recipient: Recipient, recipe: Recipe) : BasicCommand(origin, recipient) {
        override fun execute() {
            TODO("Not yet implemented")
        }
    }

    class UpdateRecipeCommand(
        origin: Origin,
        recipient: Recipient,
        private val recipePath: Path,
        private val exclusions: Set<Path>
    ) : BasicCommand(origin, recipient) {
        override fun execute() {
            debug("Creating recipe for directory «$BASE_PATH»...")
            val recWriter = StringWriter()
            val errWriter = StringWriter()
            PrintWriter(recWriter).use { recPrinter ->
                PrintWriter(errWriter).use { errPrinter ->
                    Files.walkFileTree(Path.of(BASE_PATH), RecipeVisitor(Paths.get(BASE_PATH), recPrinter, errPrinter, exclusions))
                }
            }
            debug("Recipe created.")
            val problems = errWriter.toString()
            if (problems.isNotBlank()) error(TEXT_PROBLEMS_IN_RECIPE, problems) else {
                debug("Saving recipe to «$recipePath»...")
                Recipe(recWriter.toString()).saveTo(recipePath)
                debug("Recipe saved.")
            }
            succeed()
        }
    }

}
