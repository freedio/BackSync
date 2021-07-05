package com.coradec.apps.backsync.main

import com.coradec.apps.backsync.ctrl.UpSync
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class BackSyncTest {

    @Test fun testUpSync() {
        // given
        val testee = UpSync
        // when
        // then
    }

    companion object {
        @BeforeAll
        @JvmStatic fun setup() {
            CoraModules.register(CoraComImpl(), CoraTextImpl(), CoraConfImpl(), CoraTypeImpl())
        }

        @AfterAll
        @JvmStatic fun teardown() {
            CoraModules.initialize()
        }
    }
}