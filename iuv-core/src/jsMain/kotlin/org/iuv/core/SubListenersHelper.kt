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
                subListeners.add(listener to handler)
            }

            override fun removeListener(listener: SubListener<MESSAGE>) {
                subListeners.remove(listener to handler)
            }

            override fun removeListeners() {
                subListeners.clear()
            }
        }

    override fun dispatch(t: T) {
        subListeners.forEach { it.first.onMessage(it.second.invoke(t)) }
    }

}