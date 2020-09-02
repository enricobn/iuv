package org.iuv.core.html.enums

enum class Scope(val value: String) {
        col("col"),
        colgroup("colgroup"),
        row("row"),
        rowgroup("rowgroup"),
    ;
     companion object {
            fun fromValue(value: String): Scope = values().first { it.value == value }
    }
}