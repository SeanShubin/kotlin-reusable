package com.seanshubin.kotlin.reusable.filter

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class FilterStatsImpl : FilterStats {
    private val threadSafeMatchedFilterEvents = ConcurrentLinkedQueue<MatchedFilterEvent>()
    private val threadSafeUnmatchedFilterEvents = ConcurrentLinkedQueue<UnmatchedFilterEvent>()
    private val threadSafeRegisteredPatterns = ConcurrentHashMap<String, Map<String, List<String>>>()

    override val matchedFilterEvents: List<MatchedFilterEvent>
        get() = threadSafeMatchedFilterEvents.toList()

    override val unmatchedFilterEvents: List<UnmatchedFilterEvent>
        get() = threadSafeUnmatchedFilterEvents.toList()

    override val registeredPatterns: Map<String, Map<String, List<String>>>
        get() = threadSafeRegisteredPatterns.toMap()

    override fun consumeMatchedFilterEvent(event: MatchedFilterEvent) {
        threadSafeMatchedFilterEvents.add(event)
    }

    override fun consumeUnmatchedFilterEvent(event: UnmatchedFilterEvent) {
        threadSafeUnmatchedFilterEvents.add(event)
    }

    override fun registerPatterns(category: String, patternsByType: Map<String, List<String>>) {
        threadSafeRegisteredPatterns[category] = patternsByType
    }
}
