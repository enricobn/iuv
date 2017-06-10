package org.enricobn.iuv.example

import org.enricobn.iuv.*

// MODEL
data class ButtonModel(val text: String, val selected: Boolean)

// MESSAGES
class ButtonClick

class ButtonComponent<CONTAINER_MESSAGE> : IUV<ButtonModel, ButtonClick, CONTAINER_MESSAGE>() {

    override fun update(message: ButtonClick, model: ButtonModel): ButtonModel {
        return ButtonModel(model.text, !model.selected)
    }

    override fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, model: ButtonModel, map: (ButtonClick) -> CONTAINER_MESSAGE): HTML.() -> Unit = {
        button {
            +model.text

            onClick { _ ->messageBus.send(map(ButtonClick())) }

            if (model.selected) {
                classes = "ButtonComponentSelected"
            }
        }
    }

}

fun <CONTAINER_MESSAGE> HTML.buttonComponent(messageBus: MessageBus<CONTAINER_MESSAGE>, model: ButtonModel,
                                             map: (ButtonClick) -> CONTAINER_MESSAGE) {
    ButtonComponent<CONTAINER_MESSAGE>().render(this, messageBus, model, map)
}