package com.coradec.apps.backsync.com

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.model.Recipient
import com.coradec.coradeck.com.model.impl.BasicVoucher
import com.coradec.coradeck.core.model.Origin

class DownloadRecipeVoucher(
    origin: Origin,
    val hostname: String,
    val group: String,
    target: Recipient? = null
): BasicVoucher<Recipe>(origin, target = target) {
    override fun copy(recipient: Recipient?) = DownloadRecipeVoucher(origin, hostname, group, recipient)
}
