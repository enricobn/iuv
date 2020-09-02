package org.iuv.core.html.enums

enum class Keytype(val value: String) {
        rsa("rsa"),
    ;
     companion object {
            fun fromValue(value: String): Keytype = values().first { it.value == value }
    }
}