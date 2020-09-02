package org.iuv.core.html.enums

enum class OlType(val value: String) {
        _1("1"),
        a("a"),
        A("A"),
        i("i"),
        I("I"),
    ;
     companion object {
            fun fromValue(value: String): OlType = values().first { it.value == value }
    }
}