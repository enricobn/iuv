package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.HTMLElement
import org.w3c.dom.Element
import org.w3c.dom.Node.Companion.TEXT_NODE
import org.w3c.dom.Text
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>, private val debug : Boolean = false) {

    companion object {
        val delay = 100
    }

    private val messageBus = MessageBusImpl(this::onMessage)
    private val history = mutableListOf<Pair<MESSAGE,MODEL>>()
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
        if (debug) {
            history.add(Pair(message, model))
        }
        update.second.run(messageBus)
    }

    private fun onTimer() {
        updateDocument(false)
    }

    private fun updateDocument(first: Boolean) {
        if (lastViewedModel != null && lastViewedModel!! == model) {
            return
        }
        val newView =
            if (debug) {
                val html = HTML<MESSAGE>("div")

                val init: HTML<MESSAGE>.() -> Unit = {
                    val view = iuv.view(model)
                    view.nullableMessageBus = messageBus
                    add(view)
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
                init(html)
                html
            } else {
                iuv.view(model)
            }
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

private fun toH(element: Element) : dynamic {
    val name = element.nodeName
    val data : dynamic = object {}

//    console.log("name $name")

    if (element.attributes.length > 0) {
        val attributes : dynamic = object {}
        for (i in 0 until element.attributes.length) {
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

        for (i in 0 until element.childNodes.length) {
            val child = element.childNodes[i]

            when (child) {
                is Element -> children.add(toH(child))
                is Text -> children.add(child.wholeText)
                else -> {
                    console.log("unknown type")
                    console.log(child)
                }
            }
        }

        return snabbdom.h(name, data, children.toTypedArray())
    }

}