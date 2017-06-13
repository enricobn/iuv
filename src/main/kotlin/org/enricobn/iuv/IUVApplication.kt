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

class IUVApplication<MODEL, in MESSAGE>(private val iuv: IUV<MODEL, MESSAGE, MESSAGE>) {
    private var model = iuv.init()
    private val messageBus = MessageBusImpl(this::onMessage)
    private var view : HTMLElement? = null
    private var viewH : dynamic = null

    fun run() {
        view = document.create.div()
        document.body!!.appendChild(view!!)
        updateDocument(true)
    }

    fun onMessage(message: MESSAGE) {
        val update = iuv.update(messageBus, {m -> m}, message, model)
        model = update.first
        updateDocument(false)

        if (update.second != null) {
            update.second!!.invoke()
        }
    }

    private fun updateDocument(first: Boolean) {
        val newView = HTML("div")
        iuv.view(messageBus, {m -> m }, model)(newView)

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