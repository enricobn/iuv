package org.enricobn.iuv

typealias Subscription<MESSAGE> = (MessageBus<MESSAGE>) -> Unit

interface IUV<MODEL, MESSAGE, CONTAINER_MESSAGE> : UV<MODEL, MESSAGE, CONTAINER_MESSAGE> {

    fun init() : Pair<MODEL,Subscription<MESSAGE>?>

}
