package com.seanshubin.kotlin.reusable.filter

interface FilterStats {
    val matchedFilterEvents: List<MatchedFilterEvent>
    val unmatchedFilterEvents: List<UnmatchedFilterEvent>
    val registeredPatterns: Map<String, Map<String, List<String>>> // category -> type -> patterns
    fun consumeMatchedFilterEvent(event: MatchedFilterEvent)
    fun consumeUnmatchedFilterEvent(event: UnmatchedFilterEvent)
    fun registerPatterns(category: String, patternsByType: Map<String, List<String>>)
}
