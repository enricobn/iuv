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

fun <MESSAGE> HTML<MESSAGE>.mtCard(title: String,text: String) {
        div {
            classes ="demo-card-square mdl-card mdl-shadow--2dp"
            div {
                classes ="mdl-card__title mdl-card--expand"
                +title
            }
            div {
                classes ="mdl-card__supporting-text"
                +text
            }
            div {
                classes ="mdl-card__actions mdl-card--border"
                +"Border"
            }
        }
//    <div class="demo-card-square mdl-card mdl-shadow--2dp">
//    <div class="mdl-card__title mdl-card--expand">
//    <h2 class="mdl-card__title-text">Update</h2>
//    </div>
//    <div class="mdl-card__supporting-text">
//    Lorem ipsum dolor sit amet, consectetur adipiscing elit.
//    Aenan convallis.
//    </div>
//    <div class="mdl-card__actions mdl-card--border">
//    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">
//    View Updates
//    </a>
//    </div>
//    </div>
}

