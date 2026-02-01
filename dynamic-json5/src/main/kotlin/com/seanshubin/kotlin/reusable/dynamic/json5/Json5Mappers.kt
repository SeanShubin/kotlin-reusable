package com.seanshubin.kotlin.reusable.dynamic.json5

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

object Json5Mappers {
    private val kotlinModule = KotlinModule.Builder().build()

    // Parser configured to accept JSON5 input
    val parser: ObjectMapper = JsonMapper.builder()
        .enable(JsonParser.Feature.ALLOW_COMMENTS)
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        .enable(JsonParser.Feature.ALLOW_TRAILING_COMMA)
        .build()
        .registerModule(kotlinModule)
        .registerModule(JavaTimeModule())

    // Parse JSON5 string to typed object
    inline fun <reified T> parse(json5: String): T = parser.readValue(json5)

    // Serialize to JSON5 string
    fun Any?.toJson5(): String {
        return Json5Writer.write(this)
    }

    // Parse and re-format JSON5 (normalize)
    fun String.normalizeJson5(): String {
        val asObject = parser.readValue<Any?>(this)
        return Json5Writer.write(asObject)
    }
}
