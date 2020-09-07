package org.iuv.core.html.enums

enum class ImplicitBoolean(val value: String) {
        true_("true"),
        false_("false"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): ImplicitBoolean = values().first { it.value == value }
    }
}