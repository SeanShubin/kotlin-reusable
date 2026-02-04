package com.seanshubin.kotlin.reusable.filter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FilterStatsImplTest {
    @Test
    fun matchedFilterEventsInitiallyEmpty() {
        val stats: FilterStats = FilterStatsImpl()
        assertTrue(stats.matchedFilterEvents.isEmpty())
    }

    @Test
    fun unmatchedFilterEventsInitiallyEmpty() {
        val stats: FilterStats = FilterStatsImpl()
        assertTrue(stats.unmatchedFilterEvents.isEmpty())
    }

    @Test
    fun registeredPatternsInitiallyEmpty() {
        val stats: FilterStats = FilterStatsImpl()
        assertTrue(stats.registeredPatterns.isEmpty())
    }

    @Test
    fun consumeMatchedFilterEvent() {
        val stats: FilterStats = FilterStatsImpl()
        val event1 = MatchedFilterEvent(
            category = "invocation",
            type = "boundary",
            pattern = "java\\..*",
            text = "java.lang.String"
        )
        val event2 = MatchedFilterEvent(
            category = "invocation",
            type = "core",
            pattern = "com\\.example\\..*",
            text = "com.example.MyClass"
        )

        stats.consumeMatchedFilterEvent(event1)
        stats.consumeMatchedFilterEvent(event2)

        assertEquals(2, stats.matchedFilterEvents.size)
        assertEquals(event1, stats.matchedFilterEvents[0])
        assertEquals(event2, stats.matchedFilterEvents[1])
    }

    @Test
    fun consumeUnmatchedFilterEvent() {
        val stats: FilterStats = FilterStatsImpl()
        val event1 = UnmatchedFilterEvent(
            category = "invocation",
            text = "unknown.package.Class"
        )
        val event2 = UnmatchedFilterEvent(
            category = "invocation",
            text = "another.unknown.Class"
        )

        stats.consumeUnmatchedFilterEvent(event1)
        stats.consumeUnmatchedFilterEvent(event2)

        assertEquals(2, stats.unmatchedFilterEvents.size)
        assertEquals(event1, stats.unmatchedFilterEvents[0])
        assertEquals(event2, stats.unmatchedFilterEvents[1])
    }

    @Test
    fun registerPatterns() {
        val stats: FilterStats = FilterStatsImpl()
        val patterns = mapOf(
            "boundary" to listOf("java\\..*", "javax\\..*"),
            "core" to listOf("com\\.example\\..*")
        )

        stats.registerPatterns("invocation", patterns)

        assertEquals(1, stats.registeredPatterns.size)
        assertEquals(patterns, stats.registeredPatterns["invocation"])
    }

    @Test
    fun multipleCategories() {
        val stats: FilterStats = FilterStatsImpl()

        val invocationPatterns = mapOf("boundary" to listOf("java\\..*"))
        val classPatterns = mapOf("test" to listOf(".*Test"))

        stats.registerPatterns("invocation", invocationPatterns)
        stats.registerPatterns("class", classPatterns)

        assertEquals(2, stats.registeredPatterns.size)
        assertEquals(invocationPatterns, stats.registeredPatterns["invocation"])
        assertEquals(classPatterns, stats.registeredPatterns["class"])
    }

    @Test
    fun readingEventsDoesNotModifyInternalState() {
        val stats: FilterStats = FilterStatsImpl()
        val event = MatchedFilterEvent("category", "type", "pattern", "text")

        stats.consumeMatchedFilterEvent(event)

        val firstRead = stats.matchedFilterEvents
        val secondRead = stats.matchedFilterEvents

        assertEquals(firstRead, secondRead)
        assertTrue(firstRead !== secondRead) // Different list instances
    }
}
