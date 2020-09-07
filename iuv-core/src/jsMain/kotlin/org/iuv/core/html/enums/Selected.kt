package org.iuv.core.html.enums

enum class Selected(val value: String) {
        selected("selected"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Selected = values().first { it.value == value }
    }
}