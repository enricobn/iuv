package org.enricobn.iuv

import kotlinx.html.div
import kotlinx.html.dom.create
import org.w3c.dom.HTMLElement
import org.enricobn.iuv.impl.MessageBusImpl
import kotlin.browser.document

class IUVLoop<MODEL>(private val iuv: IUV<MODEL>) {
    private var model = iuv.init()
    private val messageBus = MessageBusImpl(this::onMessage)
    private var view : HTMLElement? = null

    fun run() {
        updateDocument()
    }

    fun onMessage(message: Message) {
        model = iuv.update(message, model)
        updateDocument()
    }

    private fun updateDocument() {
        val newView = document.create.div {
            iuv.view(messageBus, model).invoke(this)
        }

        val lastView = view

        if (lastView is HTMLElement) {
            lastView.attributes.length
            lastView.remove()
        }

        view = newView
        document.body!!.appendChild(view!!)
    }

}