package com.seanshubin.kotlin.reusable.filter

class RegexFilter(
    override val type: String,
    override val pattern: String
) : Filter {
    private val regex = Regex(pattern)

    override fun matches(text: String): Boolean = regex.matches(text)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegexFilter) return false
        return type == other.type && pattern == other.pattern
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + pattern.hashCode()
        return result
    }

    override fun toString(): String = "RegexFilter(type='$type', pattern='$pattern')"
}
