package org.enricobn.iuv

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
        view?.remove()
        view = iuv.view(messageBus, model)
        document.body!!.appendChild(view!!)
    }

}