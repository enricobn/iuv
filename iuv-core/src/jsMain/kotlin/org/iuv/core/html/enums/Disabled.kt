package org.iuv.core.html.enums

enum class Disabled(val value: String) {
        disabled("disabled"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Disabled = values().first { it.value == value }
    }
}