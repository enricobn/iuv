package org.enricobn.iuv.impl

import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

class MessageBusImpl(val handler: (Message) -> Unit) : MessageBus {

    override fun send(message: Message) {
        handler.invoke(message)
    }

}