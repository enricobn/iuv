package org.enricobn.iuv

import org.enricobn.iuv.impl.MessageBusImpl
import org.w3c.dom.events.Event


external fun h(sel: String, a: dynamic, b: dynamic) : dynamic = definedExternally
external fun h(sel: String, a: dynamic) : dynamic = definedExternally
external fun h(sel: String) : dynamic = definedExternally

external fun patch(old: dynamic, new: dynamic) : Unit = definedExternally

@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class HTML<MESSAGE>(val name: String, val messageBus: MessageBus<MESSAGE>) {
    private var data : dynamic = object {}
    private val children = mutableListOf<dynamic>()
    private var text : String? = null

    fun div(init: DivH<MESSAGE>.() -> Unit) {
        val html = DivH(messageBus)
        html.init()
        children.add(html.toH())
    }

    fun td(init: TDH<MESSAGE>.() -> Unit) {
        val element = TDH(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun tr(init: TRH<MESSAGE>.() -> Unit) {
        val element = TRH(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun table(init: TableH<MESSAGE>.() -> Unit) {
        val element = TableH(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun button(init: ButtonH<MESSAGE>.() -> Unit) {
        val element = ButtonH(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun span(init: SpanH<MESSAGE>.() -> Unit) {
        val element = SpanH(messageBus)
        element.init()
        children.add(element.toH())
    }

    operator fun String.unaryPlus() {
        children.add(this)
    }

    var classes: String? = null
        set(value) {
            addAttr("class", value)
        }

    fun addAttr(name: String, attr: dynamic) {
        if (data["attrs"] == null) {
            data["attrs"] = object {}
        }
        data["attrs"][name] = attr
    }

    fun addHandler(name: String, handler: (Event) -> MESSAGE) {
        if (data["on"] == null) {
            data["on"] = object {}
        }
        data["on"][name] = { event -> messageBus.send(handler(event)) }
    }

    open fun toH() : dynamic {
        if (text != null) {
            return h(name, data, text!!)
        } else {
            return h(name, data, children.toTypedArray())
        }
    }

    fun <DEST_MESSAGE> map(f: (DEST_MESSAGE) -> MESSAGE, init: HTML<DEST_MESSAGE>.() -> Unit) : HTML<DEST_MESSAGE> {
        val destMessageBus = MessageBusImpl<DEST_MESSAGE>({message -> messageBus.send(f(message))})
        val result = HTML("div", destMessageBus)
        result.init()
        children.add(result.toH())
        return result
    }
}

fun <MESSAGE> html(name: String, messageBus: MessageBus<MESSAGE>, init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
    val element = HTML(name, messageBus)
    init.invoke(element)
    return element
}

class SpanH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("span", messageBus)

class DivH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("div", messageBus)

class TableH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("table", messageBus)

class TDH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("td", messageBus)

class TRH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("tr", messageBus)

class ButtonH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("button", messageBus) {

    fun onClick(handler: (Event) -> MESSAGE) : Unit {
        addHandler("click", handler)
    }

}

