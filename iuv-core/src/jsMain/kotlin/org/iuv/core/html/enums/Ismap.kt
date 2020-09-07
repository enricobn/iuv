package org.iuv.core.html.enums

enum class Ismap(val value: String) {
        ismap("ismap"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Ismap = values().first { it.value == value }
    }
}