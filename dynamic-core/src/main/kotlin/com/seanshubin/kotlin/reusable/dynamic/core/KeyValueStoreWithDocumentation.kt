package com.seanshubin.kotlin.reusable.dynamic.core

interface KeyValueStoreWithDocumentation {
    fun load(key: List<String>, default: Any?, documentation: List<String>): Any?
}
