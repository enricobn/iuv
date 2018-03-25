package org.iuv.core

class SubListenersHelper<T> {
    // TODO can be a Set?
    private val listeners = mutableListOf<Pair<SubListener<dynamic>,(T) -> dynamic>>()

    fun <MESSAGE> subscribe(handler: (T) -> MESSAGE) : Sub<MESSAGE> =
        object : Sub<MESSAGE> {
            override fun addListener(listener: SubListener<MESSAGE>) {
                listeners.add(Pair(listener, handler))
            }

            override fun removeListener(listener: SubListener<MESSAGE>) {
                listeners.remove(Pair(listener, handler))
            }
        }

    fun dispatch(t: T) {
        listeners.forEach { it.first.onMessage(it.second.invoke(t)) }
    }
}