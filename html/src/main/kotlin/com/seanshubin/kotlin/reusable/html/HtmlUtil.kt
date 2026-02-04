package com.seanshubin.kotlin.reusable.html

import com.seanshubin.kotlin.reusable.html.HtmlElement.Tag
import com.seanshubin.kotlin.reusable.html.HtmlElement.Text

object HtmlUtil {
    fun anchor(title: String, link: String): HtmlElement =
        Tag(
            name = "a",
            children = listOf(Text(title)),
            attributes = listOf("href" to link)
        )

    fun <T> createTable(
        list: List<T>,
        captions: List<String>,
        elementToRow: (T) -> List<String>,
        caption: String? = null
    ): List<HtmlElement> {
        val captionElement = if (caption == null) {
            emptyList<HtmlElement>()
        } else {
            listOf(Tag("p", Text("$caption count: ${list.size}")))
        }
        val theadCells = captions.map { caption ->
            Tag("th", Text(caption))
        }
        val theadRow = Tag("tr", theadCells)
        val thead = Tag("thead", theadRow)
        val tbodyRows = list.map { element ->
            val row = elementToRow(element)
            val cells = row.map { value ->
                Tag("td", Text(value))
            }
            Tag("tr", cells)
        }
        val tbody = Tag("tbody", tbodyRows)
        val table = Tag("table", thead, tbody)
        return captionElement + listOf(table)
    }

    fun listItems(items: List<String>): HtmlElement {
        val children = items.map { item ->
            Tag("li", Text(item))
        }
        return Tag("ul", children)
    }

    fun orderedListItems(items: List<String>): HtmlElement {
        val children = items.map { item ->
            Tag("li", Text(item))
        }
        return Tag("ol", children)
    }
}
