package org.enricobn.iuv

open class Message

interface MessageBus<in MESSAGE: Message> {

    fun send(message: MESSAGE)

}
