package org.iuv.core.html.enums

enum class Formnovalidate(val value: String) {
        novalidate("novalidate"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Formnovalidate = values().first { it.value == value }
    }
}