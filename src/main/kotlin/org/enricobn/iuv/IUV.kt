package org.enricobn.iuv

abstract class IUV<MODEL, MESSAGE, CONTAINER_MESSAGE> {

    abstract fun update(message: MESSAGE, model: MODEL) : MODEL

    abstract fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, model: MODEL, map: (MESSAGE) -> CONTAINER_MESSAGE): HTML.() -> Unit

    fun render(parent: HTML, messageBus: MessageBus<CONTAINER_MESSAGE>, model: MODEL, map: (MESSAGE) -> CONTAINER_MESSAGE) {
        view(messageBus, model, map)(parent)
    }

}