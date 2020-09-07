package org.iuv.core.html.enums

enum class Readonly(val value: String) {
        readonly("readonly"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Readonly = values().first { it.value == value }
    }
}