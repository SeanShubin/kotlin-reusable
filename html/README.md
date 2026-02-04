# HTML Composition Framework

This module provides a type-safe, structured way to compose HTML in Kotlin without embedding HTML as string literals.

## Purpose

When building applications that generate HTML, you need:
1. **Type-safe structure** - Catch errors at compile time, not runtime
2. **Composability** - Build complex HTML from simple parts
3. **No string concatenation** - Avoid quote escaping and syntax errors
4. **IDE support** - Refactoring, auto-completion, and navigation
5. **Testability** - Verify HTML structure programmatically

This module provides all of these capabilities through a simple, composable API.

## Core Components

### HtmlElement Interface

All HTML elements implement this interface:

```kotlin
interface HtmlElement {
    fun toLines(): List<String>
}
```

### Tag - HTML Elements

Represents an HTML tag with optional children and attributes:

```kotlin
data class Tag(
    val name: String,
    val children: List<HtmlElement> = emptyList(),
    val attributes: List<Pair<String, String>> = emptyList()
) : HtmlElement
```

### Text - Text Content

Represents text content within HTML:

```kotlin
data class Text(
    val lines: List<String>
) : HtmlElement {
    constructor(line: String) : this(listOf(line))
}
```

## Basic Usage

### Simple Tags

```kotlin
import com.seanshubin.kotlin.reusable.html.HtmlElement.Tag
import com.seanshubin.kotlin.reusable.html.HtmlElement.Text

// Empty tag
val div = Tag("div")
// Output: <div></div>

// Tag with text
val paragraph = Tag("p", Text("Hello, World!"))
// Output:
// <p>
//   Hello, World!
// </p>

// Using convenience function
val heading = HtmlElement.text("h1", "Welcome")
// Output:
// <h1>
//   Welcome
// </h1>
```

### Tags with Attributes

```kotlin
val link = Tag(
    name = "a",
    children = listOf(Text("Click here")),
    attributes = listOf(
        "href" to "https://example.com",
        "target" to "_blank"
    )
)
// Output:
// <a href="https://example.com" target="_blank">
//   Click here
// </a>
```

### Nested Tags

```kotlin
val page = Tag(
    "html",
    listOf(
        Tag(
            "head",
            listOf(Tag("title", Text("My Page")))
        ),
        Tag(
            "body",
            listOf(
                Tag("h1", Text("Welcome")),
                Tag(
                    "div",
                    listOf(
                        Tag("p", Text("First paragraph")),
                        Tag("p", Text("Second paragraph"))
                    ),
                    listOf("class" to "content")
                )
            )
        )
    )
)
```

### Vararg Constructor

For convenience, Tag has a vararg constructor:

```kotlin
val list = Tag(
    "ul",
    Tag("li", Text("Item 1")),
    Tag("li", Text("Item 2")),
    Tag("li", Text("Item 3"))
)
```

## Utility Functions

### HtmlUtil.anchor

Create links easily:

```kotlin
val link = HtmlUtil.anchor("Google", "https://www.google.com")
// Output:
// <a href="https://www.google.com">
//   Google
// </a>
```

### HtmlUtil.listItems

Create unordered lists:

```kotlin
val items = listOf("Apple", "Banana", "Cherry")
val list = HtmlUtil.listItems(items)
// Output:
// <ul>
//   <li>Apple</li>
//   <li>Banana</li>
//   <li>Cherry</li>
// </ul>
```

### HtmlUtil.orderedListItems

Create ordered lists:

```kotlin
val steps = listOf("First", "Second", "Third")
val list = HtmlUtil.orderedListItems(steps)
// Output:
// <ol>
//   <li>First</li>
//   <li>Second</li>
//   <li>Third</li>
// </ol>
```

### HtmlUtil.createTable

Create tables from data:

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(
    Person("Alice", 30),
    Person("Bob", 25)
)

val table = HtmlUtil.createTable(
    list = people,
    captions = listOf("Name", "Age"),
    elementToRow = { person -> 
        listOf(person.name, person.age.toString()) 
    },
    caption = "People"  // Optional
)
// Output:
// <p>People count: 2</p>
// <table>
//   <thead>
//     <tr>
//       <th>Name</th>
//       <th>Age</th>
//     </tr>
//   </thead>
//   <tbody>
//     <tr>
//       <td>Alice</td>
//       <td>30</td>
//     </tr>
//     <tr>
//       <td>Bob</td>
//       <td>25</td>
//     </tr>
//   </tbody>
// </table>
```

## Rendering to String

Convert HTML elements to strings:

```kotlin
val element = Tag("div", Tag("p", Text("Hello")))
val lines = element.toLines()
val html = lines.joinToString("\n")
```

Output:
```html
<div>
  <p>
    Hello
  </p>
</div>
```

## Dynamic Content

Build HTML dynamically using conditionals and loops:

```kotlin
fun createUserCard(user: User, showEmail: Boolean): HtmlElement {
    val children = mutableListOf<HtmlElement>(
        Tag("h2", Text(user.name))
    )
    
    if (showEmail) {
        children.add(Tag("p", Text(user.email)))
    }
    
    return Tag("div", children, listOf("class" to "user-card"))
}
```

## Integration with Other Modules

This module is JSON-agnostic - you can easily configure HTML generation from JSON using a separate module:

```kotlin
// In a module that depends on both html and dynamic-json
fun loadTemplate(json: Map<String, Any>): HtmlElement {
    val tagName = json["tag"] as String
    val text = json["text"] as? String
    
    return if (text != null) {
        Tag(tagName, Text(text))
    } else {
        Tag(tagName)
    }
}
```

## Design Rationale

### Why Structured Representations?

String concatenation for HTML has several problems:
- No syntax validation
- Quote escaping is error-prone
- IDE cannot refactor HTML elements
- Hard to test structure programmatically
- Easy to create malformed HTML

Structured representations solve all of these issues while remaining simple to use.

### Why toLines()?

Returning `List<String>` instead of a single string:
- Enables proper indentation
- Makes structure visible in tests
- Easy to customize output format
- Efficient for large documents (no string concatenation)

### Why Separate Tag and Text?

This mirrors HTML's actual structure - elements contain either other elements or text. The separation makes the tree structure explicit and enables type-safe composition.

## Testing

The module includes comprehensive tests (16 tests total):
- `HtmlElementTest` - 10 tests for core functionality
- `HtmlUtilTest` - 6 tests for utility functions

All tests verify both structure and indentation.

## License

See parent project UNLICENSE.txt
