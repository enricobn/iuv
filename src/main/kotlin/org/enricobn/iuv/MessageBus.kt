package org.enricobn.iuv

open class Message(val sender: IUV<*>)

interface MessageBus {

    fun send(message: Message)

}
