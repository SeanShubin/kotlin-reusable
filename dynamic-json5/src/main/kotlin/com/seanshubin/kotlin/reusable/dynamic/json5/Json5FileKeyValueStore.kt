package com.seanshubin.kotlin.reusable.dynamic.json5

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.kotlin.reusable.di.contract.FilesContract
import com.seanshubin.kotlin.reusable.dynamic.core.DynamicUtil
import com.seanshubin.kotlin.reusable.dynamic.core.KeyValueStore
import com.seanshubin.kotlin.reusable.dynamic.json5.Json5Mappers.toJson5
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class Json5FileKeyValueStore(val files: FilesContract, val path: Path) : KeyValueStore {
    override fun load(key: List<Any>): Any? {
        assertKeyValid(key)
        val json5Object = loadJson5Object()
        return DynamicUtil.get(json5Object, key)
    }

    override fun store(key: List<Any>, value: Any?) {
        assertKeyValid(key)
        val json5Object = loadJson5Object()
        val newJson5Object = DynamicUtil.set(json5Object, key, value)
        val newJson5Text = newJson5Object.toJson5()
        files.writeString(path, newJson5Text, json5Charset)
    }

    override fun exists(key: List<Any>): Boolean {
        assertKeyValid(key)
        if (!files.exists(path)) return false
        val json5Object = loadJson5Object()
        return DynamicUtil.exists(json5Object, key)
    }

    override fun arraySize(key: List<Any>): Int {
        assertKeyValid(key)
        val json5Object = loadJson5Object()
        val array = DynamicUtil.get(json5Object, key) as List<*>
        return array.size
    }

    private fun loadJson5Object(): Any? {
        val text = if (files.exists(path)) {
            files.readString(path, json5Charset)
        } else {
            defaultJson5Text
        }
        val json5Text = text.ifBlank { "{}" }
        val json5Object = Json5Mappers.parser.readValue<Any?>(json5Text)
        return json5Object
    }

    private fun assertKeyValid(key: List<Any>) {
        key.forEach(::assertKeyPartValid)
    }

    private fun assertKeyPartValid(keyPart: Any) {
        when (keyPart) {
            is String, is Int -> Unit
            else -> throw IllegalArgumentException("All key parts must be String or Int, got $keyPart")
        }
    }

    companion object Companion {
        private val json5Charset = StandardCharsets.UTF_8
        private val defaultJson5Text = "{}"
    }
}
