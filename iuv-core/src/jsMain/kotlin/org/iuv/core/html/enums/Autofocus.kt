package org.iuv.core.html.enums

enum class Autofocus(val value: String) {
        autofocus("autofocus"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Autofocus = values().first { it.value == value }
    }
}