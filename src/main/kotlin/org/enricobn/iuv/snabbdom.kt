package org.enricobn.iuv

import org.w3c.dom.events.Event


external fun h(sel: String, a: dynamic, b: dynamic) : dynamic = definedExternally
external fun h(sel: String, a: dynamic) : dynamic = definedExternally
external fun h(sel: String) : dynamic = definedExternally

external fun patch(old: dynamic, new: dynamic) : Unit = definedExternally

@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class HTML<MESSAGE>(val name: String, val messageBus: MessageBus<MESSAGE>) {
    protected val data : dynamic = object {}
    val children = mutableListOf<dynamic>()
    protected var text : String? = null

    fun div(init: DivH<MESSAGE>.() -> Unit) {
        val html = DivH<MESSAGE>(messageBus)
        html.init()
        children.add(html.toH())
    }

    fun td(init: TDH<MESSAGE>.() -> Unit) {
        val element = TDH<MESSAGE>(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun tr(init: TRH<MESSAGE>.() -> Unit) {
        val element = TRH<MESSAGE>(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun table(init: TableH<MESSAGE>.() -> Unit) {
        val element = TableH<MESSAGE>(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun button(init: ButtonH<MESSAGE>.() -> Unit) {
        val element = ButtonH<MESSAGE>(messageBus)
        element.init()
        children.add(element.toH())
    }

    fun span(init: SpanH<MESSAGE>.() -> Unit) {
        val element = SpanH<MESSAGE>(messageBus)
        element.init()
        children.add(element.toH())
    }

    operator fun String.unaryPlus() {
        children.add(this)
    }

    fun toH() : dynamic {
        if (text != null) {
            return h(name, data, text!!)
        } else {
            return h(name, data, children.toTypedArray())
        }
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

    var classes: String? = null
        set(value) {
            addAttr("class", value)
        }
}

fun <MESSAGE> html(name: String, messageBus: MessageBus<MESSAGE>, init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
    val element = HTML<MESSAGE>(name, messageBus)
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

