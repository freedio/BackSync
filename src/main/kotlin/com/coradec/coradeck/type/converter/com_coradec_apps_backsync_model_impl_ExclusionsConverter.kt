package com.coradec.coradeck.type.converter

import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.core.util.classname
import com.coradec.coradeck.type.ctrl.impl.BasicTypeConverter

class com_coradec_apps_backsync_model_impl_ExclusionsConverter: BasicTypeConverter<Exclusions>(Exclusions::class) {
    @Suppress("UNCHECKED_CAST")
    override fun convertFrom(value: Any): Exclusions? = when(value) {
        is Map<*, *> -> {
            val type = value["type"]
            val prefix = value["prefix"]
            if (type !is List<*> || (type.firstOrNull() ?: "") !is String)
                throw IllegalArgumentException("Expected string list for ‹type›!")
            if (prefix !is List<*> || (prefix.firstOrNull() ?: "") !is String)
                throw IllegalArgumentException("Expected string list for ‹prefix›!")
            Exclusions(type as List<String>, prefix as List<String>)
        }
        else -> error("Don't know how to convert from «$value» of type ‹${value::class.classname}› to Exclusions")
    }

    override fun decodeFrom(value: String): Exclusions? {
        error("Don't know how to decode from «$value» to Exclusions")
    }
}