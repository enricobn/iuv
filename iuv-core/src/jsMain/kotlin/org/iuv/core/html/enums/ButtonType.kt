package org.iuv.core.html.enums

enum class ButtonType(val value: String) {
        button("button"),
        reset("reset"),
        submit("submit"),
    ;
     companion object {
            fun fromValue(value: String): ButtonType = values().first { it.value == value }
    }
}