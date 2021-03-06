package org.iuv.examples.components

import org.iuv.core.Cmd
import org.iuv.core.Component
import org.iuv.core.HTML
import org.iuv.core.IUVRouter
import org.iuv.core.html.elements.Button

class ButtonLink(val label: String, val path: String, val extendButton: Button<Message>.() -> Unit = {}) : Component<ButtonLink.Model, ButtonLink.Message> {

    object Model

    interface Message

    object OnClick : Message

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
        when (message) {
            is OnClick -> Pair(model, IUVRouter.navigate(path))
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: Model): HTML<Message> = html {
        mtButton {
            extendButton()
            +label
            onclick(OnClick)
        }
    }

}