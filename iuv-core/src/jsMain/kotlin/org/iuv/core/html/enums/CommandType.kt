package org.iuv.core.html.enums

enum class CommandType(val value: String) {
        command("command"),
        radio("radio"),
        checkbox("checkbox"),
    ;
     companion object {
            fun fromValue(value: String): CommandType = values().first { it.value == value }
    }
}