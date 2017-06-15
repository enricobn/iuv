package org.enricobn.iuv.example

import org.enricobn.iuv.Cmd
import org.enricobn.iuv.HTML
import org.enricobn.iuv.MessageBus
import org.enricobn.iuv.UV

// MODEL
data class SelectedButtonModel(val text: String, val selected: Boolean)

// MESSAGES
interface SelectedButtonMessage

class SelectedButtonClick : SelectedButtonMessage {
    override fun toString(): String {
        return "SelectedButtonClick"
    }
}

class SelectedButton<CONTAINER_MESSAGE> : UV<SelectedButtonModel, SelectedButtonMessage, CONTAINER_MESSAGE> {

    fun init(text: String): SelectedButtonModel {
        return SelectedButtonModel(text, false)
    }

    override fun update(map: (SelectedButtonMessage) -> CONTAINER_MESSAGE, message: SelectedButtonMessage,
                        model: SelectedButtonModel): Pair<SelectedButtonModel, Cmd<CONTAINER_MESSAGE>?> {
        return Pair(SelectedButtonModel(model.text, !model.selected), null)
    }

    override fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (SelectedButtonMessage) -> CONTAINER_MESSAGE,
                      model: SelectedButtonModel): HTML.() -> Unit = {
        button {
            +model.text

            onClick { _ -> messageBus.send(map(SelectedButtonClick())) }

            if (model.selected) {
                classes = "ButtonComponentSelected"
            }
        }
    }

}

fun <CONTAINER_MESSAGE> HTML.selectedButton(messageBus: MessageBus<CONTAINER_MESSAGE>, model: SelectedButtonModel,
                                            map: (SelectedButtonMessage) -> CONTAINER_MESSAGE) {
    SelectedButton<CONTAINER_MESSAGE>().render(this, messageBus, map, model)
}