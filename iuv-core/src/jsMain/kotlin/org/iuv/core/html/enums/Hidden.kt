package org.iuv.core.html.enums

enum class Hidden(val value: String) {
        hidden("hidden"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Hidden = values().first { it.value == value }
    }
}