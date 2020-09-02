package org.iuv.core.html.enums

enum class Autocomplete(val value: String) {
        on("on"),
        off("off"),
    ;
     companion object {
            fun fromValue(value: String): Autocomplete = values().first { it.value == value }
    }
}