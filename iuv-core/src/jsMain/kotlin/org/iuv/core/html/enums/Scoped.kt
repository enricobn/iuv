package org.iuv.core.html.enums

enum class Scoped(val value: String) {
        scoped("scoped"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Scoped = values().first { it.value == value }
    }
}