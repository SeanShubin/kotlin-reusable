package com.seanshubin.kotlin.reusable.dynamic.json5

object Json5Writer {
    fun write(value: Any?, indent: Int = 0): String {
        return when (value) {
            null -> "null"
            is String -> writeString(value)
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> writeObject(value, indent)
            is List<*> -> writeArray(value, indent)
            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
    }

    private fun writeString(value: String): String {
        // Use single quotes for JSON5 style
        val escaped = value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "'$escaped'"
    }

    private fun writeObject(map: Map<*, *>, indent: Int): String {
        if (map.isEmpty()) return "{}"
        val indentStr = "  ".repeat(indent + 1)
        val closeIndentStr = "  ".repeat(indent)
        val entries = map.entries.joinToString(",\n") { (k, v) ->
            val key = if (isValidIdentifier(k.toString())) k.toString() else writeString(k.toString())
            "$indentStr$key: ${write(v, indent + 1)}"
        }
        return "{\n$entries,\n$closeIndentStr}"
    }

    private fun writeArray(list: List<*>, indent: Int): String {
        if (list.isEmpty()) return "[]"
        val indentStr = "  ".repeat(indent + 1)
        val closeIndentStr = "  ".repeat(indent)
        val items = list.joinToString(",\n") {
            "$indentStr${write(it, indent + 1)}"
        }
        return "[\n$items,\n$closeIndentStr]"
    }

    private fun isValidIdentifier(name: String): Boolean {
        if (name.isEmpty()) return false
        if (!isIdentifierStart(name[0])) return false
        return name.drop(1).all { isIdentifierPart(it) }
    }

    private fun isIdentifierStart(c: Char): Boolean {
        return c.isLetter() || c == '_' || c == '$'
    }

    private fun isIdentifierPart(c: Char): Boolean {
        return c.isLetterOrDigit() || c == '_' || c == '$'
    }
}
