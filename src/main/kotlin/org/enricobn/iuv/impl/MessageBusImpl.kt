package org.enricobn.iuv.impl

import org.enricobn.iuv.MessageBus

class MessageBusImpl<in MESSAGE>(val handler: (MESSAGE) -> Unit) : MessageBus<MESSAGE> {

    override fun send(message: MESSAGE) {
        handler(message)
    }

}