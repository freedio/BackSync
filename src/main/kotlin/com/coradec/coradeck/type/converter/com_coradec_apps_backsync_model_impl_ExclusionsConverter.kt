package com.coradec.coradeck.type.converter

import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.classname
import com.coradec.coradeck.type.ctrl.impl.BasicTypeConverter
import java.util.*

class com_coradec_apps_backsync_model_impl_ExclusionsConverter: BasicTypeConverter<Exclusions>(Exclusions::class) {
    @Suppress("UNCHECKED_CAST")
    override fun convertFrom(value: Any): Exclusions = when(value) {
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
            Exclusions(
                (type as List<String>).map { FileType.valueOf(it) }
                    .let { if (it.isEmpty()) EnumSet.noneOf(FileType::class.java) else EnumSet.copyOf(it) },
                prefix as List<String>,
                (pattern as List<String>).map { Regex(it) }
            )
        }
        else -> error("Don't know how to convert from «$value» of type ‹${value::class.classname}› to Exclusions")
    }

    override fun decodeFrom(value: String): Exclusions? {
        error("Don't know how to decode from «$value» to Exclusions")
    }
}