package com.seanshubin.kotlin.reusable.filter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FilterRegistryImplTest {
    @Test
    fun classifyWithMatch() {
        val registry = FilterRegistryImpl("invocation")
        registry.addFilter(RegexFilter("boundary", "java\\..*"))
        registry.addFilter(RegexFilter("core", "com\\.example\\..*"))

        assertEquals("boundary", registry.classify("java.lang.String"))
        assertEquals("core", registry.classify("com.example.MyClass"))
    }

    @Test
    fun classifyWithNoMatch() {
        val registry = FilterRegistryImpl("invocation")
        registry.addFilter(RegexFilter("boundary", "java\\..*"))

        assertNull(registry.classify("kotlin.String"))
    }

    @Test
    fun classifyReturnsFirstMatch() {
        val registry = FilterRegistryImpl("invocation")
        registry.addFilter(RegexFilter("boundary", ".*lang.*"))
        registry.addFilter(RegexFilter("core", "java\\..*"))

        // Should match first filter even though second also matches
        assertEquals("boundary", registry.classify("java.lang.String"))
    }

    @Test
    fun addMultipleFiltersAtOnce() {
        val registry = FilterRegistryImpl("invocation")
        val filters = listOf(
            RegexFilter("boundary", "java\\..*"),
            RegexFilter("core", "com\\.example\\..*")
        )
        registry.addFilters(filters)

        assertEquals(2, registry.filters.size)
        assertEquals("boundary", registry.classify("java.util.List"))
        assertEquals("core", registry.classify("com.example.MyClass"))
    }

    @Test
    fun categoryAccessible() {
        val registry = FilterRegistryImpl("test-category")
        assertEquals("test-category", registry.category)
    }

    @Test
    fun filtersAreDefensiveCopy() {
        val registry = FilterRegistryImpl("invocation")
        registry.addFilter(RegexFilter("boundary", "java\\..*"))

        val filters1 = registry.filters
        val filters2 = registry.filters

        assertEquals(filters1, filters2)
        assertTrue(filters1 !== filters2) // Different instances
    }

    @Test
    fun integratesWithFilterStats() {
        val stats = FilterStatsImpl()
        val registry = FilterRegistryImpl("invocation", stats)

        registry.addFilter(RegexFilter("boundary", "java\\..*"))
        registry.addFilter(RegexFilter("core", "com\\.example\\..*"))

        // Classify some items
        registry.classify("java.lang.String")
        registry.classify("com.example.MyClass")
        registry.classify("kotlin.String")

        // Check stats recorded events
        assertEquals(2, stats.matchedFilterEvents.size)
        assertEquals(1, stats.unmatchedFilterEvents.size)

        val matched = stats.matchedFilterEvents
        assertEquals("invocation", matched[0].category)
        assertEquals("boundary", matched[0].type)
        assertEquals("java\\..*", matched[0].pattern)
        assertEquals("java.lang.String", matched[0].text)

        val unmatched = stats.unmatchedFilterEvents
        assertEquals("invocation", unmatched[0].category)
        assertEquals("kotlin.String", unmatched[0].text)
    }

    @Test
    fun registersPatterns() {
        val stats = FilterStatsImpl()
        val registry = FilterRegistryImpl("invocation", stats)

        registry.addFilter(RegexFilter("boundary", "java\\..*"))
        registry.addFilter(RegexFilter("boundary", "javax\\..*"))
        registry.addFilter(RegexFilter("core", "com\\.example\\..*"))

        // Check patterns were registered
        val patterns = stats.registeredPatterns["invocation"]!!
        assertEquals(2, patterns.size)
        assertTrue(patterns.containsKey("boundary"))
        assertTrue(patterns.containsKey("core"))
        assertEquals(listOf("java\\..*", "javax\\..*"), patterns["boundary"])
        assertEquals(listOf("com\\.example\\..*"), patterns["core"])
    }
}
