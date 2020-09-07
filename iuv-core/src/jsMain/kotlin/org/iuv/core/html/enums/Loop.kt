package org.iuv.core.html.enums

enum class Loop(val value: String) {
        loop("loop"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Loop = values().first { it.value == value }
    }
}