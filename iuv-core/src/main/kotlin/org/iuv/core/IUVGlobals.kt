package org.iuv.core

import kotlin.browser.window
import kotlin.js.Date

internal object IUVGlobals {
    private var time = Date().getTime()
    private val animationFrameListeners = SubListenersHelper<Double>()
    private var animationFrameCallback: (Double) -> Unit = {}

    init {
        fun callback(t : Double) {
            animationFrameListeners.dispatch(Date().getTime() - time)
            animationFrameCallback(t)
            time = Date().getTime()
            window.requestAnimationFrame(::callback)
        }

        window.requestAnimationFrame(::callback)

    }

    var messageBus : MessageBus<Any>? = null

    fun getMessageBus() : MessageBus<Any> = messageBus!!

    fun <MESSAGE> animationFrame(handler : (Double) -> MESSAGE) : Sub<MESSAGE> {
        return animationFrameListeners.subscribe(handler)
    }

    fun setAnimationFrameCallback(callback: (Double) -> Unit) {
        this.animationFrameCallback = callback
    }

}
