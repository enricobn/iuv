package org.iuv.core.html.enums

enum class Controls(val value: String) {
        controls("controls"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Controls = values().first { it.value == value }
    }
}