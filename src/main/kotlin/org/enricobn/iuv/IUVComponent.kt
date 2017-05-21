package org.enricobn.iuv

import kotlinx.html.DIV

abstract class IUVComponent<MODEL> {

    abstract fun init() : MODEL

    abstract fun update(message: Message, model: MODEL) : MODEL

    abstract fun view(messageBus: MessageBus, model: MODEL): DIV.() -> Unit

    fun render(parent: DIV, messageBus: MessageBus, model: MODEL) {
        view(messageBus, model).invoke(parent)
    }

    val id = (IdCounter.count++).toString()

}