package org.iuv.core.html.enums

enum class MenuType(val value: String) {
        context("context"),
        toolbar("toolbar"),
    ;
     companion object {
            fun fromValue(value: String): MenuType = values().first { it.value == value }
    }
}