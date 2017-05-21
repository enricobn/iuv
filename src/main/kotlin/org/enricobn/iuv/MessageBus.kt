package org.enricobn.iuv

open class Message(val id: String)

interface MessageBus {

    fun send(message: Message)

}
