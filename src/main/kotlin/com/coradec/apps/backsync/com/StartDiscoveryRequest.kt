package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicRequest
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class StartDiscoveryRequest(
    origin: Origin,
    val root: Path,
    val recipe: Recipe,
    target: Recipient? = null
) : BasicRequest(origin, target = target) {
    override fun copy(recipient: Recipient?) = StartDiscoveryRequest(origin, root, recipe, recipient)
}
