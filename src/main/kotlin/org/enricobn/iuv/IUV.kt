package org.enricobn.iuv

import kotlinx.html.DIV

object IdCounter {
    var count = 0
}

abstract class IUV<MODEL> {

    abstract fun init() : MODEL

    abstract fun update(message: Message, model: MODEL) : MODEL

    abstract fun view(messageBus: MessageBus, model: MODEL): DIV.() -> Unit

    val id = (IdCounter.count++).toString()

}