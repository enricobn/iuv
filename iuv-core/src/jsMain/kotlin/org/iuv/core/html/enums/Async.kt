package org.iuv.core.html.enums

enum class Async(val value: String) {
        async("async"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Async = values().first { it.value == value }
    }
}