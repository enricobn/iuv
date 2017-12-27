package org.iuv.core

import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

interface DocumentEventSub<out T : Event> {
    operator fun <MESSAGE> invoke(handler : (T) -> MESSAGE) : Sub<MESSAGE>
}

interface DocumentEventSubFactory {

    val mouseMove : DocumentEventSub<MouseEvent>

}

object DocumentEventSubFactoryImpl : DocumentEventSubFactory {
    override val mouseMove = DocumentEventSubImpl<MouseEvent>("mousemove")
}

class DocumentEventSubImpl<out T : Event> internal constructor(private val name: String) : DocumentEventSub<T> {

    private var listenerAdded = false

    private val listeners = mutableListOf<Pair<SubListener<dynamic>,(T) -> dynamic>>()

    private val options : dynamic = object { val capture = true}

    fun handleEvent(event: Event) {
        listeners.forEach { (listener,handler) -> listener.onMessage(handler(event as T)) }
    }

    override operator fun <MESSAGE> invoke(handler : (T) -> MESSAGE) : Sub<MESSAGE> {
        if (!listenerAdded) {
            document.addEventListener(name, this::handleEvent, true)
            console.info("Added document listener for '$name'")
            listenerAdded = true
        }

        return object : Sub<MESSAGE> {
            override fun addListener(listener: SubListener<MESSAGE>) {
                listeners.add(Pair(listener, handler))
            }

            override fun removeListener(listener: SubListener<MESSAGE>) {
                listeners.removeAll { (_listener,_) -> listener == _listener}
//                if (listeners.isEmpty() && listenerAdded) {
//                    console.log("remove all listeners")
//                    document.removeEventListener(name, this@DocumentEventSubImpl::handleEvent, true)
//                    listenerAdded = false
//                }
            }

        }
    }

}