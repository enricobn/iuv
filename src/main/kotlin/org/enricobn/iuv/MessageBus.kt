package org.enricobn.iuv

open class Message

interface MessageBus {

    fun send(message: Message)

}
