package org.iuv.core.html.enums

enum class Checked(val value: String) {
        checked("checked"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Checked = values().first { it.value == value }
    }
}