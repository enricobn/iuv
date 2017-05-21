package org.enricobn.iuv.example

import kotlinx.html.DIV
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import org.enricobn.iuv.IUVComponent
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

data class ButtonModel(val clicked: Boolean)

class ButtonClick(_id: String) : Message(_id)

class ButtonComponent(val text: String) : IUVComponent<ButtonModel>() {

    override fun init(): ButtonModel {
        return ButtonModel(false)
    }

    override fun update(message: Message, model: ButtonModel): ButtonModel {
        var newModel = model
        if (message.id == id) {
            if (message is ButtonClick) {
                newModel = ButtonModel(!model.clicked)
            }
        }
        return newModel
    }

    override fun view(messageBus: MessageBus, model: ButtonModel): DIV.() -> Unit = {
        button {
            +text

            onClickFunction = {
                messageBus.send(ButtonClick(id))
            }
        }

        if (model.clicked) {
            div { +(text + " on") }
        }
    }

}