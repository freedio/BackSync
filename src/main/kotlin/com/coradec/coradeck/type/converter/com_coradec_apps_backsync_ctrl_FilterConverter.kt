package com.coradec.coradeck.type.converter

import com.coradec.apps.backsync.ctrl.Filter
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.type.ctrl.impl.BasicTypeConverter
import java.util.*

class com_coradec_apps_backsync_ctrl_FilterConverter : BasicTypeConverter<Filter>(Filter::class) {
    override fun convertFrom(value: Any): Filter? = when(value) {
        is Map<*, *> -> {
            val type = value["type"].let { if (it is String) listOf(it) else it }
            val prefix = value["prefix"].let { if (it is String) listOf(it) else it }
            val pattern = value["pattern"].let { if (it is String) listOf(it) else it }
            if (type !is List<*> || (type.firstOrNull() ?: "") !is String)
                throw IllegalArgumentException("Expected string list for ‹type›!")
            if (prefix !is List<*> || (prefix.firstOrNull() ?: "") !is String)
                throw IllegalArgumentException("Expected string list for ‹prefix›!")
            if (pattern !is List<*> || (pattern.firstOrNull() ?: "") !is String)
                throw IllegalArgumentException("Expected string list for ‹pattern›!")
            @Suppress("UNCHECKED_CAST")
            Filter(
                (type as List<String>).map { FileType.valueOf(it) }.toSet()
                                    .let { if (it.isEmpty()) EnumSet.noneOf(FileType::class.java) else EnumSet.copyOf(it) },
                (prefix as List<String>).toSet(),
                (pattern as List<String>).map { Regex(it) }.toSet()
            )
        }
        else -> null
    }

    override fun decodeFrom(value: String): Filter? = null
}