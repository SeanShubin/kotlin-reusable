package com.seanshubin.kotlin.reusable.dynamic.json5

import com.seanshubin.kotlin.reusable.di.contract.FilesContract
import com.seanshubin.kotlin.reusable.di.delegate.FilesDelegate
import com.seanshubin.kotlin.reusable.dynamic.core.KeyValueStoreWithDocumentationDelegate
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Json5KeyValueStoreTest {
    val files: FilesContract = FilesDelegate.defaultInstance()

    @Test
    fun intValue() {
        withTemporaryFiles { path, documentationPath ->
            val keyValueStore = Json5FileKeyValueStore(files, path)
            val documentationKeyValueStore = Json5FileKeyValueStore(files, documentationPath)
            val keyValueStoreWithDocumentation =
                KeyValueStoreWithDocumentationDelegate(keyValueStore, documentationKeyValueStore)
            val key = listOf("a", "b", "c")
            val documentation = listOf("this is a number")
            val expectedDocumentation = listOf(
                "path: a.b.c",
                "default value: 456",
                "default value type: Integer"
            ) + documentation
            val value = 456
            val actualValue = keyValueStoreWithDocumentation.load(key, value, documentation)
            val actualDocumentation = documentationKeyValueStore.load(key)
            assertEquals(expectedDocumentation, actualDocumentation)
            assertEquals(value, actualValue)
        }
    }

    @Test
    fun arrays() {
        withTemporaryFile { path ->
            val keyValueStore = Json5FileKeyValueStore(files, path)
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

    @Test
    fun json5InputParsing() {
        withTemporaryFile { path ->
            // Create a JSON5 file with various JSON5 features
            val json5Content = """
                {
                  // This is a comment
                  unquotedKey: 'single quotes',
                  'quoted-key': "double quotes",
                  number: 123,
                  trailing: 'comma',
                }
            """.trimIndent()
            files.writeString(path, json5Content, Charsets.UTF_8)

            val keyValueStore = Json5FileKeyValueStore(files, path)

            // Verify values can be accessed
            assertEquals("single quotes", keyValueStore.load(listOf("unquotedKey")))
            assertEquals("double quotes", keyValueStore.load(listOf("quoted-key")))
            assertEquals(123, keyValueStore.load(listOf("number")))
            assertEquals("comma", keyValueStore.load(listOf("trailing")))
        }
    }

    @Test
    fun json5OutputFormat() {
        withTemporaryFile { path ->
            val keyValueStore = Json5FileKeyValueStore(files, path)

            // Store some values
            keyValueStore.store(listOf("name"), "John")
            keyValueStore.store(listOf("age"), 30)
            keyValueStore.store(listOf("active"), true)

            // Read the generated file
            val content = files.readString(path, Charsets.UTF_8)

            // Verify JSON5 features in output
            assertTrue(content.contains("name:"), "Should have unquoted key 'name'")
            assertTrue(content.contains("age:"), "Should have unquoted key 'age'")
            assertTrue(content.contains("active:"), "Should have unquoted key 'active'")
            assertTrue(content.contains("'John'"), "Should use single quotes for strings")
            assertTrue(content.endsWith(",\n}"), "Should have trailing comma before closing brace")
        }
    }

    private fun withTemporaryFiles(f: (Path, Path) -> Unit) {
        withTemporaryFile { path1 ->
            withTemporaryFile { path2 ->
                f(path1, path2)
            }
        }
    }

    private fun withTemporaryFile(f: (Path) -> Unit) {
        val path = Files.createTempFile("test", ".json5")
        path.toFile().deleteOnExit()
        f(path)
        Files.delete(path)
    }
}
