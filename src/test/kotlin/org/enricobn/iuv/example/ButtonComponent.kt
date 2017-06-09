package org.enricobn.iuv.example

import org.enricobn.iuv.HTML
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

data class ButtonModel(val clicked: Boolean)

class ButtonClick(_id: String) : Message(_id)

class ButtonComponent(val text: String) : IUV<ButtonModel>() {

    override fun init(): ButtonModel {
        return ButtonModel(false)
    }

    override fun update(message: Message, model: ButtonModel): ButtonModel {
        var newModel = model
        if (message.id == ID) {
            if (message is ButtonClick) {
                newModel = ButtonModel(!model.clicked)
            }
        }
        return newModel
    }

    override fun view(messageBus: MessageBus, model: ButtonModel): HTML.() -> Unit = {
        button {
            +text

            onClick { _ ->messageBus.send(ButtonClick(ID)) }

            if (model.clicked) {
                classes("ButtonComponentSelected")
            }
        }

    }

}