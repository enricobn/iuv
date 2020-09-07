package org.iuv.core.html.enums

enum class Pubdate(val value: String) {
        pubdate("pubdate"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Pubdate = values().first { it.value == value }
    }
}