package com.seanshubin.kotlin.reusable.dynamic.json

import com.seanshubin.kotlin.reusable.di.delegate.FilesDelegate
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonKeyValueStoreTest {
    private val files = FilesDelegate.defaultInstance()

    @Test
    fun intValue() {
        withTemporaryFile { path ->
            val keyValueStore = JsonFileKeyValueStore(files, path)
            val key = listOf("a", "b", "c")
            val value = 456
            keyValueStore.store(key, value)
            val actualValue = keyValueStore.load(key)
            assertEquals(value, actualValue)
        }
    }

    @Test
    fun arrays() {
        withTemporaryFile { path ->
            val keyValueStore = JsonFileKeyValueStore(files, path)
            keyValueStore.store(listOf("the-array", 0, "name"), "a")
            keyValueStore.store(listOf("the-array", 0, "value"), 1)
            keyValueStore.store(listOf("the-array", 1, "name"), "b")
            keyValueStore.store(listOf("the-array", 1, "value"), 2)
            keyValueStore.store(listOf("the-array", 2, "name"), "c")
            keyValueStore.store(listOf("the-array", 2, "value"), 3)
            assertEquals(keyValueStore.arraySize(listOf("the-array")), 3)
            assertEquals(keyValueStore.load(listOf("the-array", 0, "name")), "a")
            assertEquals(keyValueStore.load(listOf("the-array", 0, "value")), 1)
            assertEquals(keyValueStore.load(listOf("the-array", 1, "name")), "b")
            assertEquals(keyValueStore.load(listOf("the-array", 1, "value")), 2)
            assertEquals(keyValueStore.load(listOf("the-array", 2, "name")), "c")
            assertEquals(keyValueStore.load(listOf("the-array", 2, "value")), 3)
        }
    }

    private fun withTemporaryFile(f: (Path) -> Unit) {
        val path = Files.createTempFile("test", ".json")
        path.toFile().deleteOnExit()
        f(path)
        Files.delete(path)
    }
}