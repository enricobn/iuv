package org.iuv.core.html.enums

enum class Multiple(val value: String) {
        multiple("multiple"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Multiple = values().first { it.value == value }
    }
}