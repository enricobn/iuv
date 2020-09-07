package org.iuv.core.html.enums

enum class Required(val value: String) {
        required("required"),
        empty(""),
    ;
     companion object {
            fun fromValue(value: String): Required = values().first { it.value == value }
    }
}