package com.seanshubin.kotlin.reusable.filter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegexFilterTest {
    @Test
    fun matchesPattern() {
        val filter = RegexFilter("boundary", "java\\..*")
        assertTrue(filter.matches("java.lang.String"))
        assertTrue(filter.matches("java.util.List"))
        assertFalse(filter.matches("kotlin.String"))
        assertFalse(filter.matches("com.example.MyClass"))
    }

    @Test
    fun typeAndPatternAccessible() {
        val filter = RegexFilter("core", "com\\.example\\..*")
        assertEquals("core", filter.type)
        assertEquals("com\\.example\\..*", filter.pattern)
    }

    @Test
    fun equality() {
        val filter1 = RegexFilter("boundary", "java\\..*")
        val filter2 = RegexFilter("boundary", "java\\..*")
        val filter3 = RegexFilter("core", "java\\..*")
        val filter4 = RegexFilter("boundary", "kotlin\\..*")

        assertEquals(filter1, filter2)
        assertTrue(filter1 != filter3)
        assertTrue(filter1 != filter4)
    }

    @Test
    fun hashCodeConsistent() {
        val filter1 = RegexFilter("boundary", "java\\..*")
        val filter2 = RegexFilter("boundary", "java\\..*")
        assertEquals(filter1.hashCode(), filter2.hashCode())
    }

    @Test
    fun toStringReadable() {
        val filter = RegexFilter("boundary", "java\\..*")
        val str = filter.toString()
        assertTrue(str.contains("boundary"))
        assertTrue(str.contains("java\\..*"))
    }
}
