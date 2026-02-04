# Pattern Filtering Framework with Statistics

This module provides a complete framework for pattern-based classification with built-in effectiveness tracking.

## Purpose

When building applications that classify items using patterns (regex, predicates, etc.), you need:
1. **A filtering framework** - Apply patterns and get classification results
2. **Effectiveness tracking** - Know which patterns work and which don't
3. **Gap analysis** - Find items that don't match any pattern
4. **Pattern optimization** - Identify unused or overly broad patterns

This module provides all of these capabilities in a reusable, thread-safe package.

## Core Components

### Filtering Framework

**Filter** - Interface for matching text against patterns
```kotlin
interface Filter {
    val type: String      // Classification type (e.g., "boundary", "core")
    val pattern: String   // The pattern (e.g., "java\\..*")
    fun matches(text: String): Boolean
}
```

**RegexFilter** - Regular expression based filter
```kotlin
class RegexFilter(
    override val type: String,
    override val pattern: String
) : Filter
```

**FilterRegistry** - Manages filters and performs classification
```kotlin
interface FilterRegistry {
    val category: String                    // e.g., "invocation", "class"
    val filters: List<Filter>
    fun addFilter(filter: Filter)
    fun addFilters(filters: List<Filter>)
    fun classify(text: String): String?     // Returns type or null
}
```

**FilterRegistryImpl** - Thread-safe implementation with optional stats integration
```kotlin
class FilterRegistryImpl(
    override val category: String,
    private val filterStats: FilterStats? = null
) : FilterRegistry
```

### Statistics Tracking

**Event Classes** - Record filter activity
```kotlin
data class MatchedFilterEvent(
    val category: String,   // "invocation"
    val type: String,       // "boundary"
    val pattern: String,    // "java\\..*"
    val text: String        // "java.lang.String"
)

data class UnmatchedFilterEvent(
    val category: String,   // "invocation"
    val text: String        // "unknown.package.Class"
)
```

**FilterStats** - Thread-safe statistics collection
```kotlin
interface FilterStats {
    val matchedFilterEvents: List<MatchedFilterEvent>
    val unmatchedFilterEvents: List<UnmatchedFilterEvent>
    val registeredPatterns: Map<String, Map<String, List<String>>>
    // Methods to record events and register patterns
}
```

**FilterStatsImpl** - Concurrent implementation
- Uses `ConcurrentLinkedQueue` for events
- Uses `ConcurrentHashMap` for patterns
- All reads return defensive copies
- Thread-safe for parallel processing

## Complete Usage Example

### Simple Classification

```kotlin
// Create a registry (no statistics)
val registry = FilterRegistryImpl("invocation")

// Add filters
registry.addFilters(listOf(
    RegexFilter("boundary", "java\\..*"),
    RegexFilter("boundary", "javax\\..*"),
    RegexFilter("boundary", "kotlin\\..*"),
    RegexFilter("core", "com\\.myapp\\..*"),
    RegexFilter("test", ".*Test")
))

// Classify items
val type1 = registry.classify("java.lang.String")      // Returns "boundary"
val type2 = registry.classify("com.myapp.MyClass")     // Returns "core"
val type3 = registry.classify("unknown.Class")         // Returns null
```

### Classification with Statistics

```kotlin
// Create statistics collector
val stats = FilterStatsImpl()

// Create registry with stats integration
val registry = FilterRegistryImpl("invocation", stats)

// Add filters (automatically registers with stats)
registry.addFilters(listOf(
    RegexFilter("boundary", "java\\..*"),
    RegexFilter("boundary", "javax\\..*"),
    RegexFilter("core", "com\\.myapp\\..*")
))

// Classify many items
listOf(
    "java.lang.String",
    "javax.servlet.HttpServlet",
    "com.myapp.MyClass",
    "com.myapp.Helper",
    "unknown.package.Class",
    "another.unknown.Class"
).forEach { className ->
    registry.classify(className)
}

// Analyze results
println("Total matched: ${stats.matchedFilterEvents.size}")           // 4
println("Total unmatched: ${stats.unmatchedFilterEvents.size}")       // 2
```

### Analyzing Filter Effectiveness

**Find unmatched items (gaps in coverage)**
```kotlin
val unmatchedTexts = stats.unmatchedFilterEvents
    .map { it.text }
    .distinct()
    .sorted()

println("Items with no matching pattern:")
unmatchedTexts.forEach { println("  $it") }
```

**Find unused patterns (pattern bloat)**
```kotlin
data class TypePatternKey(val type: String, val pattern: String)

val usedPatterns = stats.matchedFilterEvents
    .map { TypePatternKey(it.type, it.pattern) }
    .toSet()

val allPatterns = stats.registeredPatterns["invocation"]!!
    .flatMap { (type, patterns) ->
        patterns.map { pattern -> TypePatternKey(type, pattern) }
    }

val unusedPatterns = allPatterns.filterNot { it in usedPatterns }

println("Patterns that never matched:")
unusedPatterns.forEach { println("  ${it.type}: ${it.pattern}") }
```

**Find multi-pattern matches (pattern ambiguity)**
```kotlin
val eventsByText = stats.matchedFilterEvents.groupBy { it.text }

val ambiguousMatches = eventsByText
    .filter { (_, events) -> events.map { it.pattern }.distinct().size > 1 }

println("Items matched by multiple patterns:")
ambiguousMatches.forEach { (text, events) ->
    val patterns = events.map { it.pattern }.distinct()
    println("  $text matched by: ${patterns.joinToString(", ")}")
}
```

**Find classification conflicts (multi-type matches)**
```kotlin
val typeConflicts = eventsByText
    .filter { (_, events) -> events.map { it.type }.distinct().size > 1 }

println("Items classified as multiple types:")
typeConflicts.forEach { (text, events) ->
    val types = events.map { it.type }.distinct()
    println("  $text classified as: ${types.joinToString(", ")}")
}
```

## Multiple Categories

Track different classification systems independently:

```kotlin
val stats = FilterStatsImpl()

// Classify method invocations
val invocationRegistry = FilterRegistryImpl("invocation", stats)
invocationRegistry.addFilters(listOf(
    RegexFilter("boundary", "java\\..*"),
    RegexFilter("core", "com\\.myapp\\..*")
))

// Classify class files
val classRegistry = FilterRegistryImpl("class", stats)
classRegistry.addFilters(listOf(
    RegexFilter("test", ".*Test"),
    RegexFilter("production", ".*(?<!Test)")
))

// Use both registries
invocationRegistry.classify("java.lang.String")
classRegistry.classify("com.myapp.MyClassTest")

// Statistics track both categories separately
println("Invocation matches: ${stats.matchedFilterEvents.count { it.category == "invocation" }}")
println("Class matches: ${stats.matchedFilterEvents.count { it.category == "class" }}")
```

## Design Rationale

### Why Categories?
Categories allow tracking multiple independent classification systems in one `FilterStats` instance:
- `invocation` category: classify method calls
- `class` category: classify classes
- `file` category: classify files

Each category has its own patterns and can be analyzed independently.

### Why Thread-Safe?
Enables parallel processing of items without synchronization overhead in calling code:
```kotlin
items.parallelStream().forEach { item ->
    // No synchronization needed - FilterRegistry handles it
    registry.classify(item)
}
```

### Why Optional Statistics?
You can use the filtering framework without statistics overhead:
```kotlin
// Just classification, no tracking
val registry = FilterRegistryImpl("invocation")  // No stats parameter
```

Or enable statistics for analysis:
```kotlin
// Classification + tracking
val registry = FilterRegistryImpl("invocation", stats)
```

## Integration with Other Projects

This module extracts common filtering patterns from:
- `jvmspec` - Bytecode analysis and classification
- `inversion-guard` - Dependency inversion detection
- `code-structure` - Package dependency analysis

Each of these projects can now use this reusable module instead of maintaining duplicate filtering code.

### Migration Path

Projects using custom filtering should:
1. Add dependency: `<artifactId>kotlin-reusable-filter</artifactId>`
2. Replace custom filtering code with `FilterRegistry`
3. Change package imports to: `com.seanshubin.kotlin.reusable.filter`
4. Keep project-specific HTML reporting (can be extracted later)

## Future Enhancements

Potential additions for future versions:
- **Predicate-based filters**: Beyond regex to arbitrary predicates
- **Composite filters**: AND/OR/NOT combinations
- **Filter priorities**: Explicit ordering beyond add-order
- **Statistics queries**: Pre-built query methods for common analyses
- **HTML report generation**: Move HTML generation from projects to shared code
- **Pattern validation**: Detect malformed regex patterns at registration time
- **Performance metrics**: Track pattern matching performance

## Testing

The module includes comprehensive tests (23 tests total):
- `FilterStatsImplTest` - 10 tests for statistics collection
- `RegexFilterTest` - 5 tests for regex matching
- `FilterRegistryImplTest` - 8 tests for classification and stats integration

All tests use thread-safe implementations and verify defensive copying.

## License

See parent project UNLICENSE.txt
