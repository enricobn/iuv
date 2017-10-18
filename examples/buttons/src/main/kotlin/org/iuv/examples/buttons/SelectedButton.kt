package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.UV

// MODEL
data class SelectedButtonModel(val text: String, val selected: Boolean)

// MESSAGES
interface SelectedButtonMessage

class SelectedButtonClick : SelectedButtonMessage {
    override fun toString(): String {
        return "SelectedButtonClick"
    }
}

object SelectedButton : UV<SelectedButtonModel, SelectedButtonMessage> {

    fun init(text: String): SelectedButtonModel {
        return SelectedButtonModel(text, false)
    }

    override fun update(message: SelectedButtonMessage, model: SelectedButtonModel): Pair<SelectedButtonModel, Cmd<SelectedButtonMessage>?> {
        return Pair(SelectedButtonModel(model.text, !model.selected), null)
    }

    override fun view(model: SelectedButtonModel): HTML<SelectedButtonMessage>.() -> Unit = {
        button {
            +model.text

            onClick { _ -> SelectedButtonClick() }

            if (model.selected) {
                classes = "ButtonComponentSelected"
            }
        }
    }

}