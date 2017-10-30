package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.browser.window

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>) {

    companion object {
        val delay = 100
    }

    private val messageBus = MessageBusImpl(this::onMessage)
    private var model : MODEL
    private var lastViewedModel: MODEL? = null
    private var subscription : (() -> Unit)?
    private var view : Element? = null
    private var viewH : dynamic = null
    private val patch: (old: dynamic, new: dynamic) -> Unit = snabbdomInit()

    init {
        val init = iuv.init()
        model = init.first
        subscription = null
        init.second.run(messageBus)
    }

    fun run() {
        view = document.createElement("div")
        document.body!!.appendChild(view!!)
        updateDocument(true)
        window.setInterval(this::onTimer, delay)
    }

    private fun onMessage(message: MESSAGE) {
        val update = iuv.update(message, model)
        model = update.first
        update.second.run(messageBus)
    }

    private fun onTimer() {
        updateDocument(false)
    }

    private fun updateDocument(first: Boolean) {
        if (lastViewedModel != null && lastViewedModel!! == model) {
            return
        }

        val newView = iuv.view(model)

        newView.nullableMessageBus = messageBus

        lastViewedModel = model

        val newH = newView.toH()

        if (first) {
            patch(view!!, newH)
        } else {
            patch(viewH, newH)
        }

        viewH = newH
    }

}