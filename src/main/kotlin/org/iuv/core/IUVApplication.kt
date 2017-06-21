package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.HTMLElement
import org.w3c.dom.Element
import org.w3c.dom.Node.Companion.TEXT_NODE
import org.w3c.dom.Text
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>) {

    companion object {
        val debug = false
        val delay = 100
    }

    private val messageBus = MessageBusImpl(this::onMessage)
    private val history = mutableListOf<Pair<MESSAGE,MODEL>>()
    private var model : MODEL
    private var lastViewedModel: MODEL? = null
    private var subscription : (() -> Unit)?
    private var view : Element? = null
    private var viewH : dynamic = null

    init {
        val init = iuv.init()
        model = init.first
        subscription = null
        init.second?.invoke(messageBus)
    }

    fun run() {
        view = document.createElement("div")
        document.body!!.appendChild(view!!)
        updateDocument(messageBus, true)
        window.setInterval(this::onTimer, delay)
    }

    private fun onMessage(message: MESSAGE) {
        val update = iuv.update(message, model)
        model = update.first
        if (debug) {
            history.add(Pair(message, model))
        }
        if (update.second != null) {
            update.second!!.run(messageBus)
        }
    }

    private fun onTimer() {
        updateDocument(messageBus, false)
    }

    private fun updateDocument(messageBus: MessageBus<MESSAGE>, first: Boolean) {
        if (lastViewedModel != null && lastViewedModel!! == model) {
            return
        }
        val newView = html("div", messageBus) {
            iuv.view(model)(this)

            lastViewedModel = model

            if (debug) {
                div {
                    classes = "IUVDebugger"
                    for ((message, model) in history.takeLast(10)) {
                        button {
                            classes = "IUVDebuggerButton"
                            +(message.toString())
//                            onClick {
//                                self.model = model
//                                updateDocument(self.messageBus, false)
//                            }
                        }
                    }
                }
            }
        }

        val newH = newView.toH()

        if (first) {
            patch(view!!, newH)
        } else {
            patch(viewH, newH)
        }

        viewH = newH
    }

}

private fun toH(element: Element) : dynamic {
    val name = element.nodeName
    val data : dynamic = object {}

//    console.log("name $name")

    if (element.attributes.length > 0) {
        val attributes : dynamic = object {}
        for (i in 0..element.attributes.length - 1) {
            val attr = element.attributes[i]
            val attrName = attr?.name
            val attrValue = attr?.value
            attributes[attrName] = attrValue
//            console.log("$attrName : $attrValue")
        }
        data["attrs"] = attributes
    }

    if (element is HTMLElement) {
        element.onclick?.let {
//            console.log("onclick")
            data["on"] = object {
                val click = arrayOf(element.onclick!!)
            }
        }
    }

    if (element.nodeType == TEXT_NODE) {
        console.log("TEXT_NODE")
        return "element.innerText"
    } else {
        val children = mutableListOf<dynamic>()

//        for (i in 0..element.children.length - 1) {
//            val child = element.children.get(i)
//
//            if (child is HTMLElement) {
//                children.add(toH(child))
//            } else {
//                console.log("no HTMLElement")
//            }
//        }

        for (i in 0..element.childNodes.length - 1) {
            val child = element.childNodes[i]

            if (child is Element) {
                children.add(toH(child))
            } else if (child is Text) {
                children.add(child.wholeText)
            } else {
                console.log("unknown type")
                console.log(child)
            }
        }

        return h(name, data, children.toTypedArray())
    }

}