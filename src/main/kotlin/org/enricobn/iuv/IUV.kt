package org.enricobn.iuv

import org.w3c.dom.HTMLElement

object IdCounter {
    var count = 0
}

abstract class IUV<MODEL> {

    abstract fun init() : MODEL

    abstract fun update(message: Message, model: MODEL) : MODEL

    abstract fun view(messageBus: MessageBus, model: MODEL) : HTMLElement

    val id = (IdCounter.count++).toString()

}