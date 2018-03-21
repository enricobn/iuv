package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>,
                                        private val renderer: HTMLRenderer) : SubListener<MESSAGE> {

    companion object {
        val delay = 100
    }

    private fun messageBus(): MessageBus<MESSAGE> { return GlobalMessageBus.getMessageBus() as MessageBus<MESSAGE> }
    private var model : MODEL
    private var lastViewedModel: MODEL? = null
    private var view : Element = document.createElement("div")
    private var lastSub: Sub<MESSAGE>? = null

    init {
        try {
            GlobalMessageBus.messageBus = MessageBusImpl(this::onMessage) as MessageBus<Any>
            document.body!!.appendChild(view)
            val (newModel,cmd) = iuv.init()
            model = newModel
            cmd.run(messageBus())
        } catch (e: Exception) {
            console.error("Error in IUVApplication()", e.asDynamic().stack)
            throw e
        }
    }

    fun run() {
        updateDocument()
        window.setInterval(this::onTimer, delay)
    }

    override fun onMessage(message: MESSAGE) {
        try {
            val modelBeforeUpdate = model

            val (newModel, cmd) = iuv.update(message, model)
            model = newModel

            if (modelBeforeUpdate != model) {
                val newSub = iuv.subscriptions(model)

                lastSub.let {
                    it?.removeListener(this)
                }

                newSub.addListener(this)

                lastSub = newSub
            }

            cmd.run(messageBus())
        } catch (e: Exception) {
            console.error("Error in IUVApplication.onMessage for message '$message'.", e.asDynamic().stack)
        }
    }

    private fun onTimer() {
        updateDocument()
    }

    private fun updateDocument() {
        try {
            if (lastViewedModel != null && lastViewedModel!! == model) {
                return
            }

            // I do it before so if there's an error, the model is updated, otherwise when the updateDocument
            // is called again, by the timer, the model is still different and the error is raised again and again ...
            lastViewedModel = model

            lastViewedModel.let {
                val time = Date().getTime()
                val newView = iuv.view(it!!)

                console.log("view ${Date().getTime() - time}")

                renderer.render(view, newView)
                console.log("updateDocument ${Date().getTime() - time}")
            }
        } catch (e: Exception) {
            console.error("Error in IUVApplication.updateDocument for message '${e.message}'.", e.asDynamic().stack)
        }

    }

}