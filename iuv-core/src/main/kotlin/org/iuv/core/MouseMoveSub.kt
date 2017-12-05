package org.iuv.core

import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

object MouseMoveSub {

    private var listenerAdded = false

    private val listeners = mutableListOf<Pair<SubListener<dynamic>,(MouseEvent) -> dynamic>>()

    private fun listen(event : Event) {
        listeners.forEach { (listener,handler) -> listener.onMessage(handler(event as MouseEvent)) }
    }

    fun <MESSAGE> create(handler : (MouseEvent) -> MESSAGE) : Sub<MESSAGE> {
        if (!listenerAdded) {
            document.addEventListener("mousemove", ::listen)
        }

        return object : Sub<MESSAGE> {
            override fun addListener(listener: SubListener<MESSAGE>) {
                listeners.add(Pair(listener, handler))
            }

            override fun removeListener(listener: SubListener<MESSAGE>) {
                listeners.removeAll { (_listener,_) -> listener == _listener}
                if (listeners.isEmpty() && listenerAdded) {
                    document.removeEventListener("mousemove", ::listen)
                    listenerAdded = false
                }
            }

        }
    }

}