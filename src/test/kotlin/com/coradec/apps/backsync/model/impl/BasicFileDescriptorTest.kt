package com.coradec.apps.backsync.model.impl

import com.coradec.apps.backsync.model.FileDescriptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BasicFileDescriptorTest {

    @Test fun testEquals() {
        // given:
        val fname1 = "/tmp/hello"
        val fname2 = "/tmp/hello"
        val flastmod1 = "2021-07-06T23:10:39.150633522"
        val flastmod2 = "2021-07-06T23:10:39.150633522"
        val faccmod1 = "0644d"
        val faccmod2 = "0644d"
        val fowner1 = "1000:100"
        val fowner2 = "1000:100"
        val fsize1 = "1234"
        val fsize2 = "1234"
        val testee1 = FileDescriptor(fname1, flastmod1, faccmod1, fowner1, fsize1)
        val testee2 = FileDescriptor(fname2, flastmod2, faccmod2, fowner2, fsize2)
        // when:
        // then:
        assertThat(testee1).isEqualTo(testee2)
    }

}