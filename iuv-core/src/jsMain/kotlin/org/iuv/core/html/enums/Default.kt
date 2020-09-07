package org.iuv.core.html.enums

enum class Default(val value: String) {
        default("default"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Default = values().first { it.value == value }
    }
}