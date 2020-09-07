package org.iuv.examples.components

import org.iuv.core.html.elements.*
import org.iuv.core.html.enums.InputType

/*
    A file for Material Design Lite components.
*/

object IUVMDL {
    val dataTableNonNumeric = "mdl-data-table__cell--non-numeric"
    val isSelected = "is-selected"
    val isChecked = "is-checked"
}

fun <MESSAGE> Div<MESSAGE>.mtButton(init: Button<MESSAGE>.() -> Unit) {
    button {
        init()

        classes = classes?:"" +
            " mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect"
        //mdl-button--accent
    }

}

fun <MESSAGE> FlowContentElement<MESSAGE>.mdlTableCheckbox(labelId: String, checked: Boolean, init: Input<MESSAGE>.() -> Unit) {
    label {
        classes = classes?:"" + " mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect mdl-data-table__select"

        id = labelId

        if (checked) {
            runJs("window.document.getElementById('$labelId').MaterialCheckbox.check()")
        } else {
            runJs("window.document.getElementById('$labelId').MaterialCheckbox.uncheck()")
        }

        input {
            init()

            classes = classes?:"" + " mdl-checkbox__input"

            type = InputType.checkbox
        }

    }
}

fun <MESSAGE> Div<MESSAGE>.mdlTable(init: Table<MESSAGE>.() -> Unit) {
    table {
        init()

        classes = classes?:"" + " mdl-data-table mdl-js-data-table mdl-shadow--2dp"
    }
}

fun <MESSAGE> Div<MESSAGE>.mdlCard(title: String, text: String) {
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

