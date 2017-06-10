package org.enricobn.iuv.impl

import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

class MessageBusImpl<in MESSAGE: Message>(val handler: (MESSAGE) -> Unit) : MessageBus<MESSAGE> {

    override fun send(message: MESSAGE) {
        handler.invoke(message)
    }

}