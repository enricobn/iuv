package org.iuv.core.html.enums

enum class SandBoxAllow(val value: String) {
        allowsameorigin("allow-same-origin"),
        allowforms("allow-forms"),
        allowscripts("allow-scripts"),
        allowtopnavigation("allow-top-navigation"),
    ;
     companion object {
            fun fromValue(value: String): SandBoxAllow = values().first { it.value == value }
    }
}