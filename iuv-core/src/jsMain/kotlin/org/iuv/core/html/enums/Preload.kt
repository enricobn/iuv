package org.iuv.core.html.enums

enum class Preload(val value: String) {
        none("none"),
        metadata("metadata"),
        auto("auto"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Preload = values().first { it.value == value }
    }
}