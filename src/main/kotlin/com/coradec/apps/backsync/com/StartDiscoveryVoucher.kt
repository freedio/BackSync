package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicVoucher
import com.coradec.coradeck.core.model.Origin
import java.nio.file.Path

class StartDiscoveryVoucher(
    origin: Origin,
    val root: Path,
    val recipe: Recipe,
    target: Recipient? = null
) : BasicVoucher<Int>(origin, target = target) {
    override fun copy(recipient: Recipient?) = StartDiscoveryVoucher(origin, root, recipe, recipient)
}
