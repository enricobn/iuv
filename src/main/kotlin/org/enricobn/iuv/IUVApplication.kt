package org.enricobn.iuv

import kotlinx.html.div
import kotlinx.html.dom.create
import org.w3c.dom.HTMLElement
import org.enricobn.iuv.impl.MessageBusImpl
import org.w3c.dom.Element
import org.w3c.dom.Node.Companion.TEXT_NODE
import org.w3c.dom.Text
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.js.Date

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>) {

    companion object {
        val delay = 100
        val debug = false
    }

    private var model : MODEL
    private var subscription : (() -> Unit)?
    private val messageBus = MessageBusImpl(this::onMessage)
    private var view : HTMLElement? = null
    private var viewH : dynamic = null
    private val messagesCache = mutableListOf<MESSAGE>()
    private var time = Date().getTime()
    private var handlingMessages = false
    private val history = mutableListOf<Pair<MESSAGE,MODEL>>()
    /**
     * The next position of the message while handling messages. It's used to preserve messages order.
     */
    private var handlingMessagesPos = 0
    private val self = this

    init {
        val init = iuv.init()
        model = init.first
        subscription = null
        init.second?.invoke(messageBus)
    }

    fun run() {
        view = document.create.div()
        document.body!!.appendChild(view!!)
        updateDocument(messageBus, true)
    }

    fun onMessage(message: MESSAGE) {
        if (handlingMessages) {
            // while handling messages I collect new messages
            messagesCache.add(handlingMessagesPos, message)
            // preserving order
            handlingMessagesPos++
            return
        } else {
            messagesCache.add(message)
        }

        handlingMessages = true

        val newTime = Date().getTime()

        if (newTime - time > delay) {
            while (!messagesCache.isEmpty()) {
                val msg = messagesCache.removeAt(0)
                handlingMessagesPos = 0
                handleMessage(msg)
            }
            messagesCache.clear()
            time = newTime
        }

        handlingMessages = false
    }

    private fun handleMessage(message: MESSAGE) {
        val update = iuv.update(message, model)
        model = update.first
        if (debug) {
            history.add(Pair(message, model))
        }
        updateDocument(messageBus, false)

        if (update.second != null) {
            update.second!!(messageBus)
        }
    }

    private fun updateDocument(messageBus: MessageBus<MESSAGE>, first: Boolean) {
        val newView = html("div", messageBus) {
            iuv.view(model)(this)

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