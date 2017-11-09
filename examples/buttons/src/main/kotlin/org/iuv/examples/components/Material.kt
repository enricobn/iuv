package org.iuv.examples.components

import org.iuv.core.ButtonH
import org.iuv.core.HTML

fun <MESSAGE> HTML<MESSAGE>.mtButton(init: ButtonH<MESSAGE>.() -> Unit) {
    val button = ButtonH<MESSAGE>()

    init(button)

    val appendClasses = button.classes.let {
        if (it != null && it.isNotEmpty()) {
            " " + it
        } else {
            ""
        }
    }

    button.classes = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" + appendClasses//mdl-button--accent

    add(button)
}