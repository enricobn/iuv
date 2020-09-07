package org.iuv.core.html.enums

enum class Defer(val value: String) {
        defer("defer"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Defer = values().first { it.value == value }
    }
}