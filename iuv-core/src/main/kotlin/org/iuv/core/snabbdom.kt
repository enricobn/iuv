package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.events.Event

external object snabbdom {

    fun init(props: dynamic) : dynamic = definedExternally

    fun h(sel: String) : dynamic = definedExternally
    fun h(sel: String, a: dynamic, b: dynamic) : dynamic = definedExternally
    fun h(sel: String, a: dynamic) : dynamic = definedExternally

}

external var snabbdom_style: dynamic = definedExternally
external var snabbdom_class: dynamic = definedExternally
external var snabbdom_props: dynamic = definedExternally
external var snabbdom_attributes: dynamic = definedExternally
external var snabbdom_eventlisteners: dynamic = definedExternally

fun snabbdomInit() : ((old: dynamic, new: dynamic) -> Unit) {
    val props = arrayOf(
            snabbdom_style.default,
            snabbdom_class.default,
            snabbdom_props.default,
            snabbdom_attributes.default,
            snabbdom_eventlisteners.default
    )
    return snabbdom.init(props)
}

@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class HTML<MESSAGE>(val name: String, private val messageBus: MessageBus<MESSAGE>) {
    private var data : dynamic = object {}
    private val children = mutableListOf<dynamic>()
    private var text : String? = null

    fun div(init: DivH<MESSAGE>.() -> Unit) {
        element(DivH(messageBus), init)
    }

    fun td(init: TDH<MESSAGE>.() -> Unit) {
        element(TDH(messageBus), init)
    }

    fun tr(init: TRH<MESSAGE>.() -> Unit) {
        element(TRH(messageBus), init)
    }

    fun table(init: TableH<MESSAGE>.() -> Unit) {
        element(TableH(messageBus), init)
    }

    fun button(init: ButtonH<MESSAGE>.() -> Unit) {
        element(ButtonH(messageBus), init)
    }

    fun span(init: SpanH<MESSAGE>.() -> Unit) {
        element(SpanH(messageBus), init)
    }

    fun thead(init: TheadH<MESSAGE>.() -> Unit) {
        element(TheadH(messageBus), init)
    }

    fun th(init: THH<MESSAGE>.() -> Unit) {
        element(THH(messageBus), init)
    }

    fun input(init: InputH<MESSAGE>.() -> Unit) {
        element(InputH(messageBus), init)
    }

    fun b(init: BH<MESSAGE>.() -> Unit) {
        element(BH(messageBus), init)
    }

    private fun <ELEMENT: HTML<MESSAGE>> element(element: ELEMENT, init: ELEMENT.() -> Unit) {
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

    var style: String? = null
        set(value) {
            addAttr("style", value)
        }

    fun addAttr(name: String, attr: dynamic) {
        if (data["attrs"] == null) {
            data["attrs"] = object {}
        }
        data["attrs"][name] = attr
    }

    fun <EVENT : Event> addHandler(name: String, handler: (EVENT) -> MESSAGE) {
        if (data["on"] == null) {
            data["on"] = object {}
        }
        data["on"][name] = { event -> messageBus.send(handler(event)) }
    }

    open fun toH() : dynamic {
        return if (text != null) {
            snabbdom.h(name, data, text!!)
        } else {
            snabbdom.h(name, data, children.toTypedArray())
        }
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> childView(uv: UV<CHILD_MODEL, CHILD_MESSAGE>,
                                                model: CHILD_MODEL,
                                                mapFun: (CHILD_MESSAGE) -> MESSAGE) {

        val init: HTML<CHILD_MESSAGE>.() -> Unit = {
            uv.view(model)(this)
        }

        map(mapFun, init)
    }

    private fun <CHILD_MESSAGE> map(mapFun: (CHILD_MESSAGE) -> MESSAGE, init: HTML<CHILD_MESSAGE>.() -> Unit) {
        val destMessageBus = MessageBusImpl<CHILD_MESSAGE>({ messageBus.send(mapFun(it)) })
        val result = HTML("div", destMessageBus)

        result.init()

        /* In this way I throw away the div and, with it, all it's attributes.
         *
         * For example in the uv.view:
         * view(...) {
         *      classes = "AClass"
         *      button {
         *          ...
         *      }
         * }
         *
         * I throw away even the classes.
         * I think it's not bad, since I'm changing the container's attributes, and if it's what I want,
         * I can wrap all in a div:
         *
         * view(...) {
         *      div {
         *          classes = "AClass"
         *          button {
         *              ...
         *          }
         *      }
         * }
         *
         * but it's different from what happens in the top level UV (IUV).
         */
        children.addAll(result.children)

        // I don't throw away the div!
        // children.add(result.toH())
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

class TheadH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("thead", messageBus)

class THH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("th", messageBus)

class BH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("b", messageBus)

class TDH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("td", messageBus) {

    fun onClick(handler: (Event) -> MESSAGE) {
        addHandler("click", handler)
    }

}

class TRH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("tr", messageBus)

data class InputEvent(val value: String)

class InputH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("input", messageBus) {
    var value: String = ""
        set(value) {
            addAttr("value", value)
        }

    var autofocus: Boolean = false
        set(value) {
            if (value) {
                addAttr("autofocus", "autofocus")
            }
        }

    fun onInput(handler: (InputEvent) -> MESSAGE) {
        addHandler("input", { event: Event ->
            handler(InputEvent(event.target?.asDynamic().value))
        })
    }

    fun onBlur(handler: (InputEvent) -> MESSAGE) {
        addHandler("blur", { event: Event ->
            handler(InputEvent(event.target?.asDynamic().value))
        })
    }

}

class ButtonH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("button", messageBus) {

    fun onClick(handler: (Event) -> MESSAGE) {
        addHandler("click", handler)
    }

}

