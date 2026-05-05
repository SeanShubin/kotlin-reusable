package com.seanshubin.kotlin.reusable.fileselection

import com.seanshubin.kotlin.reusable.di.delegate.FilesDelegate
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals

class FileChooserImplIntegrationTest {
    private val fileChooser = FileChooserImpl(FilesDelegate.defaultInstance())

    @Test
    fun includeByExtension() {
        withTempDir { baseDir ->
            createFile(baseDir, "Foo.kt")
            createFile(baseDir, "Bar.kt")
            createFile(baseDir, "config.json")

            val chosen = fileChooser.choose(
                FileSelection(baseDir, includePatterns = listOf(".*\\.kt"))
            ).toRelative(baseDir)

            assertEquals(listOf("Bar.kt", "Foo.kt"), chosen)
        }
    }

    @Test
    fun excludeOverridesInclude() {
        withTempDir { baseDir ->
            createFile(baseDir, "Main.kt")
            createFile(baseDir, "MainTest.kt")

            val chosen = fileChooser.choose(
                FileSelection(
                    baseDir,
                    includePatterns = listOf(".*\\.kt"),
                    excludePatterns = listOf(".*Test\\.kt")
                )
            ).toRelative(baseDir)

            assertEquals(listOf("Main.kt"), chosen)
        }
    }

    @Test
    fun includesFilesInSubdirectories() {
        withTempDir { baseDir ->
            Files.createDirectories(baseDir.resolve("src/main"))
            createFile(baseDir, "src/main/Foo.kt")
            createFile(baseDir, "src/main/Bar.kt")

            val chosen = fileChooser.choose(
                FileSelection(baseDir, includePatterns = listOf(".*\\.kt"))
            ).toRelative(baseDir)

            assertEquals(listOf("src/main/Bar.kt", "src/main/Foo.kt"), chosen)
        }
    }

    @Test
    fun skipDirectoryExcludesEntireSubtree() {
        withTempDir { baseDir ->
            Files.createDirectories(baseDir.resolve("src"))
            Files.createDirectories(baseDir.resolve("target"))
            createFile(baseDir, "src/Main.kt")
            createFile(baseDir, "target/Main.class")
            createFile(baseDir, "target/Main.kt")

            val chosen = fileChooser.choose(
                FileSelection(
                    baseDir,
                    includePatterns = listOf(".*\\.kt"),
                    skipDirectoryPatterns = listOf("target")
                )
            ).toRelative(baseDir)

            assertEquals(listOf("src/Main.kt"), chosen)
        }
    }

    @Test
    fun skipDirectoryMatchesNestedDirectories() {
        withTempDir { baseDir ->
            Files.createDirectories(baseDir.resolve("a/target"))
            Files.createDirectories(baseDir.resolve("a/src"))
            createFile(baseDir, "a/src/Foo.kt")
            createFile(baseDir, "a/target/Foo.kt")

            val chosen = fileChooser.choose(
                FileSelection(
                    baseDir,
                    includePatterns = listOf(".*\\.kt"),
                    skipDirectoryPatterns = listOf(".*/target")
                )
            ).toRelative(baseDir)

            assertEquals(listOf("a/src/Foo.kt"), chosen)
        }
    }

    @Test
    fun emptyBaseDirReturnsNoFiles() {
        withTempDir { baseDir ->
            val chosen = fileChooser.choose(
                FileSelection(baseDir, includePatterns = listOf(".*"))
            )
            assertEquals(emptyList(), chosen)
        }
    }

    @Test
    fun noMatchingIncludePatternReturnsNoFiles() {
        withTempDir { baseDir ->
            createFile(baseDir, "Foo.kt")

            val chosen = fileChooser.choose(
                FileSelection(baseDir, includePatterns = listOf(".*\\.java"))
            )
            assertEquals(emptyList(), chosen)
        }
    }

    private fun withTempDir(block: (Path) -> Unit) {
        val tempDir = Files.createTempDirectory("file-selection-test")
        try {
            block(tempDir)
        } finally {
            deleteRecursively(tempDir)
        }
    }

    private fun createFile(baseDir: Path, relativePath: String) {
        val file = baseDir.resolve(relativePath)
        Files.createDirectories(file.parent)
        Files.createFile(file)
    }

    private fun List<Path>.toRelative(baseDir: Path): List<String> =
        map { baseDir.relativize(it).toString().replace('\\', '/') }.sorted()

    private fun deleteRecursively(path: Path) {
        if (Files.isDirectory(path)) {
            Files.list(path).use { stream -> stream.forEach { deleteRecursively(it) } }
        }
        Files.deleteIfExists(path)
    }
}
