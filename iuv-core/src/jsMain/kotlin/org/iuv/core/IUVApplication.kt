package org.iuv.core

import kotlinx.browser.document
import org.iuv.core.impl.MessageBusImpl
import kotlin.js.Date

class IUVApplication<MODEL, in MESSAGE>(private val view: View<MODEL, MESSAGE>,
                                        private val renderer: HTMLRenderer) : SubListener<MESSAGE> {

    private var model : MODEL
    private var lastViewedModel: MODEL? = null
    private val mainElement = document.createElement("div")
    private var lastSub: Sub<MESSAGE>? = null

    init {
        try {
            document.body!!.appendChild(mainElement)

            IUVGlobals.messageBus = MessageBusImpl(this::onMessage) as MessageBus<Any>

            val (newModel,cmd) = view.init()
            model = newModel
            cmd.run(messageBus())
        } catch (e: Exception) {
            console.error("Error in IUVApplication()", e.asDynamic().stack)
            throw e
        }
    }

    fun run() {
        IUVGlobals.setAnimationFrameCallback { updateDocument() }
    }

    override fun onMessage(message: MESSAGE) {
        try {
            val modelBeforeUpdate = model

            val (newModel, cmd) = view.update(message, model)
            model = newModel

            if (modelBeforeUpdate != model) {
                val newSub = view.subscriptions(model)

                lastSub.let {
                    it?.removeListeners()
                }

                newSub.addListener(this)

                lastSub = newSub
            }

            cmd.run(messageBus())
        } catch (e: Exception) {
            console.error("Error in IUVApplication.onMessage for message '$message'.", e.asDynamic().stack)
            throw e
        }
    }

    private fun messageBus(): MessageBus<MESSAGE> { return IUVGlobals.getMessageBus() as MessageBus<MESSAGE> }

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
                val newView = view.view(it!!)

                if (printTime)
                    console.log("view ${Date().getTime() - time}")

                renderer.render(mainElement, newView)

                if (printTime)
                    console.log("updateDocument ${Date().getTime() - time}")
            }
        } catch (e: Exception) {
            console.error("Error in IUVApplication.updateDocument for message '${e.message}'.", e.asDynamic().stack)
            throw e
        }

    }

}