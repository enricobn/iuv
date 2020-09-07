package org.iuv.core.html.enums

enum class Reversed(val value: String) {
        reversed("reversed"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Reversed = values().first { it.value == value }
    }
}