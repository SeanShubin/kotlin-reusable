package com.seanshubin.kotlin.reusable.table.format

import java.io.BufferedReader
import java.io.Reader

interface TableFormatter {
    interface Justify {
        data class Left(val x: Any?) : Justify

        data class Right(val x: Any?) : Justify
    }

    fun format(originalRows: List<List<Any?>>): List<String>
    fun <T> parse(reader: Reader, mapToElement:(Map<String, String>) -> T):Iterable<T>

    companion object {
        private fun String.truncate(max: Int): String =
            if (this.length > max) "<${this.length} characters, showing first $max> ${this.substring(0, max)}"
            else this
        private fun String.escape(): String = this.flatMap(::escapeCharToIterable).joinToString("")
        private fun escapeCharToIterable(target: Char): Iterable<Char> = escapeCharToString(target).asIterable()
        private fun escapeCharToString(target: Char): String =
            when (target) {
                '\n' -> "\\n"
                '\b' -> "\\b"
                '\t' -> "\\t"
                '\r' -> "\\r"
                '\"' -> "\\\""
                '\'' -> "\\\'"
                '\\' -> "\\\\"
                else -> target.toString()
            }

        val escapeString: (Any?) -> String = { cell ->
            when (cell) {
                null -> "null"
                else -> cell.toString().escape()
            }
        }

        fun escapeAndTruncateString(max: Int): (Any?) -> String = { cell ->
            escapeString(cell).truncate(max)
        }

        fun Reader.toBufferedReader(): BufferedReader = BufferedReader(this)
        fun <T> List<List<T>>.transpose(): List<List<T>> {
            return if (this.isEmpty()) {
                emptyList()
            } else {
                val mutableList = mutableListOf<List<T>>()
                for (i in 0..this[0].lastIndex) {
                    val newMutableRow = mutableListOf<T>()
                    for (j in 0..this.lastIndex) {
                        newMutableRow.add(this[j][i])
                    }
                    mutableList.add(newMutableRow)
                }
                mutableList
            }
        }
    }
}
