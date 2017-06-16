package org.enricobn.iuv

typealias Subscription<MESSAGE> = (MessageBus<MESSAGE>) -> Unit

interface IUV<MODEL, MESSAGE> : UV<MODEL, MESSAGE> {

    fun init() : Pair<MODEL,Subscription<MESSAGE>?>

}
