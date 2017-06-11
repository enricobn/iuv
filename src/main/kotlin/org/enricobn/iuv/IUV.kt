package org.enricobn.iuv

abstract class IUV<MODEL, MESSAGE, CONTAINER_MESSAGE> {

    abstract fun update(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE, message: MESSAGE, model: MODEL) : Pair<MODEL,(() -> Unit)?>

    abstract fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE, model: MODEL): HTML.() -> Unit

    fun render(parent: HTML, messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE, model: MODEL) {
        view(messageBus, map, model)(parent)
    }

}