package com.seanshubin.kotlin.reusable.filter

data class MatchedFilterEvent(
    val category: String,
    val type: String,
    val pattern: String,
    val text: String
)
