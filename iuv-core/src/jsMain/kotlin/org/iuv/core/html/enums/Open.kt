package org.iuv.core.html.enums

enum class Open(val value: String) {
        open_("open"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Open = values().first { it.value == value }
    }
}