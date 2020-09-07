package org.iuv.core.html.enums

enum class Autoplay(val value: String) {
        autoplay("autoplay"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Autoplay = values().first { it.value == value }
    }
}