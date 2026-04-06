package com.seanshubin.kotlin.reusable.zip

import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
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
        val testData = createTestZipData()
        val inputStream = ByteArrayInputStream(testData)
        val iterator: Iterator<ZipContents> =
            ZipContentsIterator(inputStream, name, ::isZip, ZipContentsIterator.AcceptAll)

        val actual =
            iterator.asSequence().flatMap(::operateOnCursor).map(::scrubLine).joinToString("\n")
        assertEquals(expected, actual)
    }

    fun scrubLine(line: String): String = line.replace("\\\\", "/")

    private fun createTestZipData(): ByteArray {
        val zipC = createZip {
            addDirectory("dir-c/")
            addFile("dir-c/file-f.txt", "Hello F!")
            addFile("dir-c/file-g.txt", "Hello G!")
        }

        val zipD = createZip {
            addDirectory("dir-d/")
            addFile("dir-d/file-h.txt", "Hello H!")
        }

        val zipE = createZip {
            addDirectory("dir-e/")
            addFile("dir-e/file-i.txt", "Hello I!")
            addFile("dir-e/file-j.txt", "Hello J!")
        }

        val zipB = createZip {
            addDirectory("dir-b/")
            addFile("dir-b/file-d.txt", "Hello D!")
            addFile("dir-b/file-e.txt", "Hello E!")
            addZipFile("dir-b/zip-c.zip", zipC)
            addZipFile("dir-b/zip-d.zip", zipD)
            addZipFile("dir-b/zip-e.zip", zipE)
        }

        val zipF = createZip {
            addDirectory("dir-f/")
            addFile("dir-f/file-k.txt", "Hello K!")
        }

        val zipA = createZip {
            addDirectory("dir-a/")
            addFile("dir-a/file-c.txt", "Hello C!")
            addZipFile("dir-a/zip-b.zip", zipB)
            addZipFile("dir-a/zip-f.zip", zipF)
        }

        val zipG = createZip {
            addDirectory("dir-g/")
        }

        return createZip {
            addFile("file-a.txt", "Hello A!")
            addFile("file-b.txt", "Hello B!")
            addZipFile("zip-a.zip", zipA)
            addZipFile("zip-g.zip", zipG)
        }
    }

    private fun createZip(builder: ZipBuilder.() -> Unit): ByteArray {
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zipOut ->
            ZipBuilder(zipOut).builder()
        }
        return output.toByteArray()
    }

    private class ZipBuilder(private val zipOut: ZipOutputStream) {
        fun addFile(name: String, content: String) {
            zipOut.putNextEntry(ZipEntry(name))
            zipOut.write(content.toByteArray(StandardCharsets.UTF_8))
            zipOut.closeEntry()
        }

        fun addZipFile(name: String, content: ByteArray) {
            zipOut.putNextEntry(ZipEntry(name))
            zipOut.write(content)
            zipOut.closeEntry()
        }

        fun addDirectory(name: String) {
            zipOut.putNextEntry(ZipEntry(name))
            zipOut.closeEntry()
        }
    }
}
