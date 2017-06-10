package org.enricobn.iuv

interface MessageBus<in MESSAGE> {

    fun send(message: MESSAGE)

}
