package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.events.Event

external object snabbdom {

    fun init(props: dynamic) : dynamic = definedExternally

}

external var snabbdom_style: dynamic = definedExternally
external var snabbdom_class: dynamic = definedExternally
external var snabbdom_props: dynamic = definedExternally
external var snabbdom_attributes: dynamic = definedExternally
external var snabbdom_eventlisteners: dynamic = definedExternally

fun snabbdomInit() : ((old: dynamic, new: dynamic) -> Unit) {
    val props = arrayOf(
            org.iuv.core.snabbdom_style,
            org.iuv.core.snabbdom_class,
            org.iuv.core.snabbdom_props,
            org.iuv.core.snabbdom_attributes,
            org.iuv.core.snabbdom_eventlisteners
    )
    return snabbdom.init(props)
}

external fun h(sel: String) : dynamic = definedExternally
external fun h(sel: String, a: dynamic, b: dynamic) : dynamic = definedExternally
external fun h(sel: String, a: dynamic) : dynamic = definedExternally

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
            h(name, data, text!!)
        } else {
            h(name, data, children.toTypedArray())
        }
    }

    fun <COMPONENT_MODEL,COMPONENT_MESSAGE> map(uv: UV<COMPONENT_MODEL, COMPONENT_MESSAGE>,
                                                model: COMPONENT_MODEL,
                                                mapFun: (COMPONENT_MESSAGE) -> MESSAGE) {

        val init: HTML<COMPONENT_MESSAGE>.() -> Unit = {
            uv.view(model)(this)
        }

        map(mapFun, init)
    }

    fun <CONTAINER_MESSAGE> map(mapFun: (CONTAINER_MESSAGE) -> MESSAGE, init: HTML<CONTAINER_MESSAGE>.() -> Unit) {
        val destMessageBus = MessageBusImpl<CONTAINER_MESSAGE>({ messageBus.send(mapFun(it)) })
        val result = HTML("div", destMessageBus)

        result.init()

        /* I throw away the div and, with it, all it's attributes.
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

class TDH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("td", messageBus)

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

