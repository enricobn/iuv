package org.iuv.core.html.enums

enum class HttpEquiv(val value: String) {
        contentlanguage("content-language"),
        contenttype("content-type"),
        defaultstyle("default-style"),
        refresh("refresh"),
        setcookie("set-cookie"),
    ;
     companion object {
            fun fromValue(value: String): HttpEquiv = values().first { it.value == value }
    }
}