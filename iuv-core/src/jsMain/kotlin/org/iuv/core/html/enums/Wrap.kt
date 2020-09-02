package org.iuv.core.html.enums

enum class Wrap(val value: String) {
        hard("hard"),
        soft("soft"),
    ;
     companion object {
            fun fromValue(value: String): Wrap = values().first { it.value == value }
    }
}