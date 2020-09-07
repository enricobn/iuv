package org.iuv.core.html.enums

enum class Formenctype(val value: String) {
        application_xwwwformurlencoded("application/x-www-form-urlencoded"),
        multipart_formdata("multipart/form-data"),
        text_plain("text/plain"),
    ;
     companion object {
            fun fromValue(value: String): Formenctype = values().first { it.value == value }
    }
}