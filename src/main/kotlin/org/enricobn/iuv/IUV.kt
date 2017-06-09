package org.enricobn.iuv

object IdCounter {
    var count = 0
}

abstract class IUV<MODEL> {

    abstract fun init() : MODEL

    abstract fun update(message: Message, model: MODEL) : MODEL

    abstract fun view(messageBus: MessageBus, model: MODEL): HTML.() -> Unit

    fun render(parent: HTML, messageBus: MessageBus, model: MODEL) {
        view(messageBus, model).invoke(parent)
    }

    val ID = (IdCounter.count++).toString()

}