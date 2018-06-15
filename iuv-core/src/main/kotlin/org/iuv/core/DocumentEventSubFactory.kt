package org.iuv.core

import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

interface DocumentEventSub<out T : Event> {
    operator fun <MESSAGE> invoke(handler : (T) -> MESSAGE) : Sub<MESSAGE>
}

interface DocumentEventSubFactory {

    val mouseMove : DocumentEventSub<MouseEvent>

    val keydown : DocumentEventSub<KeyboardEvent>

    val keyup : DocumentEventSub<KeyboardEvent>

    fun <MESSAGE> animationFrame(handler: (Double) -> MESSAGE): Sub<MESSAGE>
}

object DocumentEventSubFactoryImpl : DocumentEventSubFactory {

    override val mouseMove = DocumentEventSubImpl<MouseEvent>("mousemove")

    override val keydown = DocumentEventSubImpl<KeyboardEvent>("keydown")

    override val keyup = DocumentEventSubImpl<KeyboardEvent>("keyup")

    override fun  <MESSAGE> animationFrame(handler : (Double) -> MESSAGE) : Sub<MESSAGE> {
        return IUVGlobals.animationFrame(handler)
    }
}

class DocumentEventSubImpl<out T : Event> internal constructor(private val name: String) : DocumentEventSub<T> {

    private var listenerAdded = false

    private val listeners = SubListenersHelper<T>()

    fun handleEvent(event: Event) {
        //val mouseEvent = event as MouseEvent
        //console.log("Dispatched ${mouseEvent.screenX},${mouseEvent.screenY}")
        listeners.dispatch(event as T)
    }

    override operator fun <MESSAGE> invoke(handler : (T) -> MESSAGE) : Sub<MESSAGE> {
        if (!listenerAdded) {
            document.addEventListener(name, this::handleEvent, true)
            console.info("Added document listener for '$name'")
            listenerAdded = true
        }

        return listeners.subscribe(handler)
    }

}