package com.teaops.shared.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatUtilsTest {

    @Test
    fun testPad2() {
        assertEquals("00", 0L.pad2())
        assertEquals("05", 5L.pad2())
        assertEquals("12", 12L.pad2())
        assertEquals("123", 123L.pad2())
    }

    @Test
    fun testRoundToOneDecimal() {
        assertEquals(0.0, 0.0.roundToOneDecimal())
        assertEquals(1.2, 1.23.roundToOneDecimal())
        assertEquals(1.3, 1.25.roundToOneDecimal())
        assertEquals(1.3, 1.28.roundToOneDecimal())
        assertEquals(-1.2, (-1.23).roundToOneDecimal())
        assertEquals(-1.3, (-1.25).roundToOneDecimal())
    }
}
