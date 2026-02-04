package com.seanshubin.kotlin.reusable.filter

class FilterRegistryImpl(
    override val category: String,
    private val filterStats: FilterStats? = null
) : FilterRegistry {
    private val mutableFilters = mutableListOf<Filter>()

    override val filters: List<Filter>
        get() = mutableFilters.toList()

    override fun addFilter(filter: Filter) {
        mutableFilters.add(filter)
        registerPatternsWithStats()
    }

    override fun addFilters(filters: List<Filter>) {
        mutableFilters.addAll(filters)
        registerPatternsWithStats()
    }

    override fun classify(text: String): String? {
        for (filter in mutableFilters) {
            if (filter.matches(text)) {
                filterStats?.consumeMatchedFilterEvent(
                    MatchedFilterEvent(
                        category = category,
                        type = filter.type,
                        pattern = filter.pattern,
                        text = text
                    )
                )
                return filter.type
            }
        }
        filterStats?.consumeUnmatchedFilterEvent(
            UnmatchedFilterEvent(category = category, text = text)
        )
        return null
    }

    private fun registerPatternsWithStats() {
        filterStats?.let { stats ->
            val patternsByType = mutableFilters
                .groupBy { it.type }
                .mapValues { (_, filters) -> filters.map { it.pattern } }
            stats.registerPatterns(category, patternsByType)
        }
    }
}
