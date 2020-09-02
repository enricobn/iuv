package org.iuv.core.html.enums

enum class Shape(val value: String) {
        rect("rect"),
        circle("circle"),
        poly("poly"),
        default("default"),
    ;
     companion object {
            fun fromValue(value: String): Shape = values().first { it.value == value }
    }
}