package org.iuv.core.html.enums

enum class InputType(val value: String) {
        button("button"),
        checkbox("checkbox"),
        color("color"),
        date("date"),
        datetime("datetime"),
        datetimelocal("datetime-local"),
        email("email"),
        file("file"),
        hidden("hidden"),
        image("image"),
        month("month"),
        number("number"),
        password("password"),
        radio("radio"),
        range("range"),
        reset("reset"),
        search("search"),
        submit("submit"),
        text("text"),
        tel("tel"),
        time("time"),
        url("url"),
        week("week"),
    ;
     companion object {
            fun fromValue(value: String): InputType = values().first { it.value == value }
    }
}