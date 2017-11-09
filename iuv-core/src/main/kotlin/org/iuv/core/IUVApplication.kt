package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.browser.window

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>,
                                        private val renderer: HTMLRenderer) {

    companion object {
        val delay = 100
    }

    private val messageBus = MessageBusImpl(this::onMessage)
    private var model : MODEL
    private var lastViewedModel: MODEL? = null
    private var subscription : (() -> Unit)?
    private var view : Element? = null

    init {
        val init = iuv.init()
        model = init.first
        subscription = null
        init.second.run(messageBus)
    }

    fun run() {
        view = document.createElement("div")
        document.body!!.appendChild(view!!)
        updateDocument()
        window.setInterval(this::onTimer, delay)
    }

    private fun onMessage(message: MESSAGE) {
        val update = iuv.update(message, model)
        model = update.first
        update.second.run(messageBus)
    }

    private fun onTimer() {
        updateDocument()
    }

    private fun updateDocument() {
        if (lastViewedModel != null && lastViewedModel!! == model) {
            return
        }

        val newView = iuv.view(model)

        newView.nullableMessageBus = messageBus

        lastViewedModel = model

        renderer.render(view!!, newView)
    }

}