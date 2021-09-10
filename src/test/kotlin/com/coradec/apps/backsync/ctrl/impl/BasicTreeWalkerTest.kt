package com.coradec.apps.backsync.ctrl.impl

import com.coradec.apps.backsync.model.Recipe
import com.coradec.apps.backsync.model.impl.Exclusions
import com.coradec.coradeck.com.module.CoraComImpl
import com.coradec.coradeck.conf.module.CoraConfImpl
import com.coradec.coradeck.core.util.FileType.DIRECTORY
import com.coradec.coradeck.ctrl.module.CoraControlImpl
import com.coradec.coradeck.dir.model.module.CoraModules
import com.coradec.coradeck.text.module.CoraTextImpl
import com.coradec.coradeck.type.module.impl.CoraTypeImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import java.util.*

internal class BasicTreeWalkerTest {

    @Test fun testFull() {
        // given
        val root = Paths.get("/")
        val testee = BasicTreeWalker(root, Recipe())
        // when
        val r1 = testee.next
        val r2 = testee.next
        val r3 = testee.next
        val r4 = testee.next
        val r5 = testee.next
        val r6 = testee.next
        // then
        assertThat(r1).isEqualTo(root)
        assertThat(r2).isEqualTo(root.resolve("boot"))
        assertThat(r3).isEqualTo(root.resolve("boot/efi"))
        assertThat(r4).isEqualTo(root.resolve("boot/grub"))
        assertThat(r5).isEqualTo(root.resolve("boot/grub/gfxblacklist.txt"))
        assertThat(r6).isEqualTo(root.resolve("boot/grub/unicode.pf2"))
    }

    @Test fun testExclusionsByType() {
        // given
        val root = Paths.get("/")
        val testee = BasicTreeWalker(root, Recipe(Exclusions(type = EnumSet.of(DIRECTORY))))
        // when
        val r1 = testee.next
        val r2 = testee.next
        val r3 = testee.next
        val r4 = testee.next
        val r5 = testee.next
        val r6 = testee.next
        // then
        assertThat(r1).isEqualTo(root.resolve("boot/grub/gfxblacklist.txt"))
        assertThat(r2).isEqualTo(root.resolve("boot/grub/unicode.pf2"))
        assertThat(r3).isEqualTo(root.resolve("boot/grub/x86_64-efi/core.efi"))
        assertThat(r4).isEqualTo(root.resolve("boot/grub/x86_64-efi/grub.efi"))
        assertThat(r5).isEqualTo(root.resolve("boot/grub/x86_64-efi/acpi.mod"))
        assertThat(r6).isEqualTo(root.resolve("boot/grub/x86_64-efi/adler32.mod"))
    }

    @Test fun testExclusionsByPrefix() {
        // given
        val root = Paths.get("/")
        val testee = BasicTreeWalker(root, Recipe(Exclusions(prefix = listOf("/boot", "/home"))))
        // when
        val r1 = testee.next
        val r2 = testee.next
        val r3 = testee.next
        val r4 = testee.next
        val r5 = testee.next
        val r6 = testee.next
        // then
        assertThat(r1).isEqualTo(root)
        assertThat(r2).isEqualTo(root.resolve("etc"))
        assertThat(r3).isEqualTo(root.resolve("etc/GNUstep"))
        assertThat(r4).isEqualTo(root.resolve("etc/GNUstep/GNUstep.conf"))
        assertThat(r5).isEqualTo(root.resolve("etc/GNUstep/gdomap_probes"))
        assertThat(r6).isEqualTo(root.resolve("etc/NetworkManager"))
    }

    companion object {
        init {
            CoraModules.register(CoraConfImpl(), CoraComImpl(), CoraTextImpl(), CoraTypeImpl(), CoraControlImpl())
        }
    }
}