package org.iuv.examples.components

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View

class ComponentsView : View<ComponentsView.Model, ComponentsView.Message> {
    private val buttonLink = ButtonLink("Mario", "/mario")

    data class Model(val buttonLinkModel : ButtonLink.Model)

    interface Message

    data class ButtonLinkMessage(val message: ButtonLink.Message) : Message

    override fun init(): Pair<Model, Cmd<Message>> =
        Pair(Model(ButtonLink.Model), Cmd.none())

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
        when (message) {
            is ButtonLinkMessage -> {
                val (buttonLinkModel, buttonLinkCmd) =
                        buttonLink.update(message.message, model.buttonLinkModel)
                Pair(model.copy(buttonLinkModel = buttonLinkModel), buttonLinkCmd.map(::ButtonLinkMessage))
            }
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: Model): HTML<Message> = html {
        +"ButtonLink "
        add(buttonLink.view(model.buttonLinkModel), ::ButtonLinkMessage)
    }

}