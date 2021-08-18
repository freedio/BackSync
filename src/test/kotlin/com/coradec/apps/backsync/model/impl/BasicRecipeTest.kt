package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.util.FileType
import com.coradec.coradeck.core.util.FileType.*
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BasicRecipeTest {

    @Test
    fun test() {
        // given:
        val testee = Recipe()
        // when:
        // then:
        assertThat(testee.exclusions.type).hasSameElementsAs(
            setOf(SOCKET, BLOCKDEVICE, CHARDEVICE, PIPE, DOOR, LOST_LINK).map { it }
        )
        assertThat(testee.exclusions.prefix).hasSameElementsAs(setOf(
            "/dev", "/media", "/mnt", "/proc", "/run", "/sys", "/tmp", "/var"
        ))
    }

    companion object {
        init {
            CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
        }
    }
}

