package org.iuv.core

import org.iuv.core.html.elements.Div

interface Component<MODEL, MESSAGE> {

    fun subscriptions(model: MODEL) : Sub<MESSAGE> = Sub.none()

    fun update(message: MESSAGE, model: MODEL) : Pair<MODEL, Cmd<MESSAGE>>

    fun view(model: MODEL): HTML<MESSAGE>

    fun html(init: Div<MESSAGE>.() -> Unit) : Div<MESSAGE> {
        val html = Div<MESSAGE>()
        init(html)
        return html
    }

    fun sendMessage(msg: MESSAGE) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            messageBus.send(msg)
        }
    }

    fun <CHILD_MODEL, CHILD_MESSAGE> childComponentBuilder(component: Component<CHILD_MODEL, CHILD_MESSAGE>)
            where CHILD_MESSAGE : Any =
        ChildComponentBuilder.of<MODEL, MESSAGE, CHILD_MODEL, CHILD_MESSAGE>(component)

}

