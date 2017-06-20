package org.iuv.core

interface MessageBus<in MESSAGE> {

    fun send(message: MESSAGE)

}
