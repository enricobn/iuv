package org.iuv.core.impl

import org.iuv.core.MessageBus

class MessageBusImpl<in MESSAGE>(private val handler: (MESSAGE) -> Unit) : MessageBus<MESSAGE> {

    override fun send(message: MESSAGE) {
        handler(message)
    }

}