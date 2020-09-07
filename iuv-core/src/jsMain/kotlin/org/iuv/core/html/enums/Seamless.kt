package org.iuv.core.html.enums

enum class Seamless(val value: String) {
        seamless("seamless"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Seamless = values().first { it.value == value }
    }
}