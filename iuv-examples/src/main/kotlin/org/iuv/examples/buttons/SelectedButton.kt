package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.Component
import org.iuv.core.HTML

// MODEL
data class SelectedButtonModel(val text: String, val selected: Boolean)

// MESSAGES
interface SelectedButtonMessage

object SelectedButtonClick : SelectedButtonMessage {
    override fun toString(): String {
        return "SelectedButtonClick"
    }
}

object SelectedButton : Component<SelectedButtonModel, SelectedButtonMessage> {

    fun init(text: String): SelectedButtonModel {
        return SelectedButtonModel(text, false)
    }

    override fun update(message: SelectedButtonMessage, model: SelectedButtonModel): Pair<SelectedButtonModel, Cmd<SelectedButtonMessage>> {
        return Pair(SelectedButtonModel(model.text, !model.selected), Cmd.none())
    }

    override fun view(model: SelectedButtonModel): HTML<SelectedButtonMessage> {
        return html {
            button {
                +model.text

                onClick { _ -> SelectedButtonClick }

                if (model.selected) {
                    classes = "ButtonComponentSelected"
                }
            }
        }
    }

}