package com.seanshubin.kotlin.reusable.fileselection

import com.seanshubin.kotlin.reusable.di.test.FilesUnsupportedOperation
import org.junit.Test
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import kotlin.test.assertEquals

class FileChooserImplTest {
    @Test
    fun includeByExtension() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addFile("Foo.kt")
        stub.addFile("Bar.kt")
        stub.addFile("config.json")
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(baseDir, includePatterns = listOf(".*\\.kt"))
        ).toRelative(baseDir)

        assertEquals(listOf("Bar.kt", "Foo.kt"), chosen)
    }

    @Test
    fun excludeOverridesInclude() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addFile("Main.kt")
        stub.addFile("MainTest.kt")
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(
                baseDir,
                includePatterns = listOf(".*\\.kt"),
                excludePatterns = listOf(".*Test\\.kt")
            )
        ).toRelative(baseDir)

        assertEquals(listOf("Main.kt"), chosen)
    }

    @Test
    fun includesFilesInSubdirectories() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addDir("src")
        stub.addDir("src/main")
        stub.addFile("src/main/Foo.kt")
        stub.addFile("src/main/Bar.kt")
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(baseDir, includePatterns = listOf(".*\\.kt"))
        ).toRelative(baseDir)

        assertEquals(listOf("src/main/Bar.kt", "src/main/Foo.kt"), chosen)
    }

    @Test
    fun skipDirectoryExcludesEntireSubtree() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addDir("src")
        stub.addDir("target")
        stub.addFile("src/Main.kt")
        stub.addFile("target/Main.class")
        stub.addFile("target/Main.kt")
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(
                baseDir,
                includePatterns = listOf(".*\\.kt"),
                skipDirectoryPatterns = listOf("target")
            )
        ).toRelative(baseDir)

        assertEquals(listOf("src/Main.kt"), chosen)
    }

    @Test
    fun skipDirectoryMatchesNestedDirectories() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addDir("a")
        stub.addDir("a/target")
        stub.addDir("a/src")
        stub.addFile("a/src/Foo.kt")
        stub.addFile("a/target/Foo.kt")
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(
                baseDir,
                includePatterns = listOf(".*\\.kt"),
                skipDirectoryPatterns = listOf(".*/target")
            )
        ).toRelative(baseDir)

        assertEquals(listOf("a/src/Foo.kt"), chosen)
    }

    @Test
    fun emptyBaseDirReturnsNoFiles() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(baseDir, includePatterns = listOf(".*"))
        )

        assertEquals(emptyList(), chosen)
    }

    @Test
    fun noMatchingIncludePatternReturnsNoFiles() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addFile("Foo.kt")
        val fileChooser = FileChooserImpl(stub)

        val chosen = fileChooser.choose(
            FileSelection(baseDir, includePatterns = listOf(".*\\.java"))
        )

        assertEquals(emptyList(), chosen)
    }

    @Test
    fun notifyOnInclude() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addFile("Main.kt")
        val events = mutableListOf<String>()
        val notify = RecordingNotify(events)
        val fileChooser = FileChooserImpl(stub, notify)

        fileChooser.choose(FileSelection(baseDir, includePatterns = listOf(".*\\.kt")))

        assertEquals(listOf("include Main.kt .*\\.kt"), events)
    }

    @Test
    fun notifyOnExclude() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addFile("MainTest.kt")
        val events = mutableListOf<String>()
        val notify = RecordingNotify(events)
        val fileChooser = FileChooserImpl(stub, notify)

        fileChooser.choose(
            FileSelection(
                baseDir,
                includePatterns = listOf(".*\\.kt"),
                excludePatterns = listOf(".*Test\\.kt")
            )
        )

        assertEquals(listOf("exclude MainTest.kt .*Test\\.kt"), events)
    }

    @Test
    fun notifyOnUnmatched() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addFile("config.json")
        val events = mutableListOf<String>()
        val notify = RecordingNotify(events)
        val fileChooser = FileChooserImpl(stub, notify)

        fileChooser.choose(FileSelection(baseDir, includePatterns = listOf(".*\\.kt")))

        assertEquals(listOf("unmatched config.json"), events)
    }

    @Test
    fun notifyOnSkipDirectory() {
        val baseDir = Paths.get("fake-base")
        val stub = FileSystemStub(baseDir)
        stub.addDir("target")
        stub.addFile("target/Main.class")
        val events = mutableListOf<String>()
        val notify = RecordingNotify(events)
        val fileChooser = FileChooserImpl(stub, notify)

        fileChooser.choose(
            FileSelection(
                baseDir,
                includePatterns = listOf(".*"),
                skipDirectoryPatterns = listOf("target")
            )
        )

        assertEquals(listOf("skipDir target target"), events)
    }

    private fun List<Path>.toRelative(baseDir: Path): List<String> =
        map { baseDir.relativize(it).toString().replace('\\', '/') }.sorted()
}

private class RecordingNotify(private val events: MutableList<String>) : FileSelectionNotify {
    override fun onInclude(relativePath: String, pattern: String) {
        events.add("include $relativePath $pattern")
    }

    override fun onExclude(relativePath: String, pattern: String) {
        events.add("exclude $relativePath $pattern")
    }

    override fun onUnmatched(relativePath: String) {
        events.add("unmatched $relativePath")
    }

    override fun onSkipDirectory(relativePath: String, pattern: String) {
        events.add("skipDir $relativePath $pattern")
    }
}

private class FileSystemStub(private val baseDir: Path) : FilesUnsupportedOperation() {
    private val dirs = mutableListOf<Path>()
    private val files = mutableListOf<Path>()

    fun addDir(relativePath: String) {
        dirs.add(baseDir.resolve(relativePath))
    }

    fun addFile(relativePath: String) {
        files.add(baseDir.resolve(relativePath))
    }

    override fun walkFileTree(start: Path, visitor: FileVisitor<in Path>): Path {
        walkDir(start, visitor)
        return start
    }

    private fun walkDir(dir: Path, visitor: FileVisitor<in Path>): FileVisitResult {
        val result = visitor.preVisitDirectory(dir, fakeAttrs)
        if (result == FileVisitResult.SKIP_SUBTREE) return FileVisitResult.CONTINUE
        if (result == FileVisitResult.TERMINATE) return FileVisitResult.TERMINATE
        for (subDir in dirs.filter { it.parent == dir }.sortedBy { it.toString() }) {
            val r = walkDir(subDir, visitor)
            if (r == FileVisitResult.TERMINATE) return r
        }
        for (file in files.filter { it.parent == dir }.sortedBy { it.toString() }) {
            val r = visitor.visitFile(file, fakeAttrs)
            if (r == FileVisitResult.TERMINATE) return r
        }
        return visitor.postVisitDirectory(dir, null)
    }

    private val fakeAttrs = object : BasicFileAttributes {
        override fun lastModifiedTime(): FileTime = throw UnsupportedOperationException()
        override fun lastAccessTime(): FileTime = throw UnsupportedOperationException()
        override fun creationTime(): FileTime = throw UnsupportedOperationException()
        override fun isRegularFile(): Boolean = throw UnsupportedOperationException()
        override fun isDirectory(): Boolean = throw UnsupportedOperationException()
        override fun isSymbolicLink(): Boolean = throw UnsupportedOperationException()
        override fun isOther(): Boolean = throw UnsupportedOperationException()
        override fun size(): Long = throw UnsupportedOperationException()
        override fun fileKey(): Any? = throw UnsupportedOperationException()
    }
}
