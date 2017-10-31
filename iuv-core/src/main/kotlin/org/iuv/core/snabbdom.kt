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
open class HTML<MESSAGE>(private val name: String) {
    private var data : dynamic = object {}
    private val children = mutableListOf<dynamic>()
    private var text : String? = null
    internal var nullableMessageBus : MessageBus<MESSAGE>? = null
    private val messageBus: MessageBus<MESSAGE> = object : MessageBus<MESSAGE> {
        override fun send(message: MESSAGE) {
            nullableMessageBus!!.send(message)
        }
    }

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

//    fun <CHILD_MESSAGE> add(html: HTML<CHILD_MESSAGE>, mapFun: (CHILD_MESSAGE) -> MESSAGE) {
//        val newMessageBus = MessageBusImpl<CHILD_MESSAGE> {message -> messageBus.send(mapFun.invoke(message))}
//        html.nullableMessageBus = newMessageBus
//        children.add(html.toH())
//    }
//
//    internal fun add(html: HTML<MESSAGE>) {
//        children.addAll(html.children)
//    }

    fun <PARENT_MESSAGE> map(parent: HTML<PARENT_MESSAGE>, mapFun: (MESSAGE) -> PARENT_MESSAGE) {
        val newMessageBus = MessageBusImpl<MESSAGE> {message -> parent.messageBus.send(mapFun.invoke(message))}
        nullableMessageBus = newMessageBus

        parent.children.addAll(children)
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

//    fun <CHILD_MODEL,CHILD_MESSAGE> childView(uv: UV<CHILD_MODEL, CHILD_MESSAGE>,
//                                                model: CHILD_MODEL,
//                                                mapFun: (CHILD_MESSAGE) -> MESSAGE) {
//
//        val init: HTML<CHILD_MESSAGE>.() -> Unit = {
//            uv.view(model)(this)
//        }
//
//        map(mapFun, init)
//    }
//
//    private fun <CHILD_MESSAGE> map(mapFun: (CHILD_MESSAGE) -> MESSAGE, init: HTML<CHILD_MESSAGE>.() -> Unit) {
//        val destMessageBus = MessageBusImpl<CHILD_MESSAGE>({ messageBus.send(mapFun(it)) })
//        val result = HTML<CHILD_MESSAGE>("div")
//        result.nullableMessageBus = destMessageBus
//
//        result.init()
//
//        /* In this way I throw away the div and, with it, all it's attributes.
//         *
//         * For example in the uv.view:
//         * view(...) {
//         *      classes = "AClass"
//         *      button {
//         *          ...
//         *      }
//         * }
//         *
//         * I throw away even the classes.
//         * I think it's not bad, since I'm changing the container's attributes, and if it's what I want,
//         * I can wrap all in a div:
//         *
//         * view(...) {
//         *      div {
//         *          classes = "AClass"
//         *          button {
//         *              ...
//         *          }
//         *      }
//         * }
//         *
//         * but it's different from what happens in the top level UV (IUV).
//         */
//        children.addAll(result.children)
//
//        // I don't throw away the div!
//        // children.add(result.toH())
//    }

}

class SpanH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("span") {
    init {
        nullableMessageBus = messageBus
    }
}

class DivH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("div") {
    init {
        nullableMessageBus = messageBus
    }
}

class TableH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("table") {
    init {
        nullableMessageBus = messageBus
    }
}

class TheadH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("thead") {
    init {
        nullableMessageBus = messageBus
    }
}

class THH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("th") {
    init {
        nullableMessageBus = messageBus
    }
}

class BH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("b") {
    init {
        nullableMessageBus = messageBus
    }
}

class TDH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("td") {

    init {
        nullableMessageBus = messageBus
    }

    fun onClick(handler: (Event) -> MESSAGE) {
        addHandler("click", handler)
    }

}

class TRH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("tr") {
    init {
        nullableMessageBus = messageBus
    }
}

data class InputEvent(val value: String)

class InputH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("input") {

    init {
        nullableMessageBus = messageBus
    }

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

    // TODO enum?
    var type: String = "text"
        set(value) {
            addAttr("type", value)
        }

    var min: Int? = null
        set(value) {
            if (value != null) {
                addAttr("min", value.toString())
            }
        }

    var max: Int? = null
        set(value) {
            if (value != null) {
                addAttr("max", value.toString())
            }
        }

    var step: Int? = null
        set(value) {
            if (value != null) {
                addAttr("step", value.toString())
            }
        }

    fun onInput(handler: (InputEvent) -> MESSAGE) {
        addHandler("input", { event: Event ->
            handler(InputEvent(event.target?.asDynamic().value))
        })
        addHandler("change", { event: Event ->
            handler(InputEvent(event.target?.asDynamic().value))
        })
    }

    fun onBlur(handler: (InputEvent) -> MESSAGE) {
        addHandler("blur", { event: Event ->
            handler(InputEvent(event.target?.asDynamic().value))
        })
    }

}

class ButtonH<MESSAGE>(messageBus: MessageBus<MESSAGE>) : HTML<MESSAGE>("button") {

    init {
        nullableMessageBus = messageBus
    }

    fun onClick(handler: (Event) -> MESSAGE) {
        addHandler("click", handler)
    }

}

