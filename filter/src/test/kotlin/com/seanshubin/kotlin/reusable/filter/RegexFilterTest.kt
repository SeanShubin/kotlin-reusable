package com.seanshubin.kotlin.reusable.filter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegexFilterTest {
    @Test
    fun matchesPattern() {
        val filter = RegexFilter("boundary", "java\\..*")
        assertTrue(filter.matches(BOUNDARY_CLASS))
        assertTrue(filter.matches(ANOTHER_BOUNDARY_CLASS))
        assertFalse(filter.matches(STDLIB_CLASS))
        assertFalse(filter.matches(CORE_CLASS))
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

    companion object {
        const val BOUNDARY_CLASS = "java.lang.String"
        const val ANOTHER_BOUNDARY_CLASS = "java.util.List"
        const val STDLIB_CLASS = "kotlin.String"
        const val CORE_CLASS = "com.example.MyClass"
    }
}
