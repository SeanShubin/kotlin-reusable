package com.seanshubin.kotlin.reusable.zip

import org.junit.Test
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals

class ZipContentsIteratorTest {
    @Test
    fun iterator() {
        val expected = """
            data.zip/file-a.txt
              Hello A!
            data.zip/file-b.txt
              Hello B!
            data.zip/zip-a.zip/dir-a/
            data.zip/zip-a.zip/dir-a/file-c.txt
              Hello C!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/file-d.txt
              Hello D!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/file-e.txt
              Hello E!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-c.zip/dir-c/
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-c.zip/dir-c/file-f.txt
              Hello F!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-c.zip/dir-c/file-g.txt
              Hello G!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-d.zip/dir-d/
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-d.zip/dir-d/file-h.txt
              Hello H!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-e.zip/dir-e/
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-e.zip/dir-e/file-i.txt
              Hello I!
            data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-e.zip/dir-e/file-j.txt
              Hello J!
            data.zip/zip-a.zip/dir-a/zip-f.zip/dir-f/
            data.zip/zip-a.zip/dir-a/zip-f.zip/dir-f/file-k.txt
              Hello K!
            data.zip/zip-g.zip/dir-g/
        """.trimIndent()

        fun isZip(name: String) = name.endsWith(".zip")

        fun operateOnCursor(cursor: ZipContents): List<String> {
            val (path, zipEntry, bytes) = cursor
            val pathString = (path + zipEntry.name).joinToString("/")
            return if (zipEntry.isDirectory) {
                listOf(pathString)
            } else {
                val content = String(bytes.toByteArray(), StandardCharsets.UTF_8)
                listOf(pathString, "  $content")
            }
        }

        val name = "data.zip"
        val inputStream = javaClass.classLoader.getResourceAsStream(name)
        val iterator: Iterator<ZipContents> =
            ZipContentsIterator(inputStream, name, ::isZip, ZipContentsIterator.AcceptAll)

        val actual =
            iterator.asSequence().flatMap(::operateOnCursor).map(::scrubLine).joinToString("\n")
        assertEquals(expected, actual)
    }

    fun scrubLine(line: String): String = line.replace("\\\\", "/")
}
