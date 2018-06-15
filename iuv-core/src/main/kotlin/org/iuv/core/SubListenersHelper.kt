package org.iuv.core

interface Dispatcher<in T> {

    fun dispatch(t: T)

}

class SubListenersHelper<T> : Dispatcher<T> {
    // TODO can be a Set?
    private val subListeners = mutableListOf<Pair<SubListener<dynamic>,(T) -> dynamic>>()

    fun <MESSAGE> subscribe(handler: (T) -> MESSAGE) : Sub<MESSAGE> =
        object : Sub<MESSAGE> {
            override fun addListener(listener: SubListener<MESSAGE>) {
                subListeners.add(Pair(listener, handler))
            }

            override fun removeListener(listener: SubListener<MESSAGE>) {
                subListeners.remove(Pair(listener, handler))
            }
        }

    override fun dispatch(t: T) {
        subListeners.forEach { it.first.onMessage(it.second.invoke(t)) }
    }

}