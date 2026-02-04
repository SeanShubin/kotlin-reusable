package com.seanshubin.kotlin.reusable.filter

interface FilterRegistry {
    val category: String
    val filters: List<Filter>
    fun addFilter(filter: Filter)
    fun addFilters(filters: List<Filter>)
    fun classify(text: String): String?
}
