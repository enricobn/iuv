package org.enricobn.iuv.example

import org.enricobn.iuv.HTML
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

// MODEL
data class ButtonModel(val selected: Boolean)

class ButtonComponent(val text: String) : IUV<ButtonModel>() {
    val self = this

    // MESSAGES
    inner class ButtonClick : Message(self)

    override fun init(): ButtonModel {
        return ButtonModel(false)
    }

    override fun update(message: Message, model: ButtonModel): ButtonModel {
        var newModel = model
        if (message.sender == this) {
            if (message is ButtonClick) {
                newModel = ButtonModel(!model.selected)
            }
        }
        return newModel
    }

    override fun view(messageBus: MessageBus, model: ButtonModel): HTML.() -> Unit = {
        button {
            +text

            onClick { _ ->messageBus.send(ButtonClick()) }

            if (model.selected) {
                classes = "ButtonComponentSelected"
            }
        }

    }

}