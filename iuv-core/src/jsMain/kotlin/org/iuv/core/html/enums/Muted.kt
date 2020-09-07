package org.iuv.core.html.enums

enum class Muted(val value: String) {
        muted("muted"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Muted = values().first { it.value == value }
    }
}