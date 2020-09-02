package org.iuv.core.html.enums

enum class Dir(val value: String) {
        ltr("ltr"),
        rtl("rtl"),
        auto("auto"),
    ;
     companion object {
            fun fromValue(value: String): Dir = values().first { it.value == value }
    }
}