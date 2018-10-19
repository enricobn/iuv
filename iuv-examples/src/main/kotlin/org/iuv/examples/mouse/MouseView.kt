package org.iuv.examples.mouse

import org.iuv.core.*

class MouseView : View<MouseView.Model,MouseView.Message> {
    data class Model(val x: Int, val y: Int)

    interface Message

    private data class MouseMove(val x: Int, val y: Int) : Message

    override fun subscriptions(model: Model): Sub<Message> {
        return DocumentEventSubFactoryImpl.mouseMove { MouseMove(it.screenX, it.screenY) }
    }

    override fun init(): Pair<Model, Cmd<Message>> =
        Pair(Model(0, 0), Cmd.none())

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
        when (message) {
            is MouseMove -> Pair(model.copy(x = message.x, y = message.y), Cmd.none())
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: Model): HTML<Message> = html {
        +"${model.x},${model.y}"
    }

}