package org.iuv.core.html.enums

enum class Formmethod(val value: String) {
        get("get"),
        post("post"),
    ;
     companion object {
            fun fromValue(value: String): Formmethod = values().first { it.value == value }
    }
}