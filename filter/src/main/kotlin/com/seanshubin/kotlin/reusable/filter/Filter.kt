package com.seanshubin.kotlin.reusable.filter

interface Filter {
    val type: String
    val pattern: String
    fun matches(text: String): Boolean
}
