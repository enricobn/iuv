package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.dom.Element
import org.w3c.dom.events.Event

@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class HTML<MESSAGE>(val name: String) : HTMLChild {
    private val attrs = mutableMapOf<String,dynamic>()
    private val handlers = mutableMapOf<String,dynamic>()
    private val children = mutableListOf<HTMLChild>()
    private var text : String? = null
    internal var nullableMessageBus : MessageBus<MESSAGE>? = null

    private val messageBus: MessageBus<MESSAGE> = object : MessageBus<MESSAGE> {
        override fun send(message: MESSAGE) {
            nullableMessageBus!!.send(message)
        }
    }

    fun getChildren() = children.toList()

    fun getAttrs() = attrs.toMap()

    fun getHandlers() = handlers.toMap()

    fun hasHandler(name: String) = handlers.containsKey(name)

    fun gethandler(name: String) = handlers[name]

    fun getText() = text

    fun div(init: DivH<MESSAGE>.() -> Unit) {
        element(DivH(), init)
    }

    fun td(init: TDH<MESSAGE>.() -> Unit) {
        element(TDH(), init)
    }

    fun tr(init: TRH<MESSAGE>.() -> Unit) {
        element(TRH(), init)
    }

    fun table(init: TableH<MESSAGE>.() -> Unit) {
        element(TableH(), init)
    }

    fun button(init: ButtonH<MESSAGE>.() -> Unit) {
        element(ButtonH(), init)
    }

    fun span(init: SpanH<MESSAGE>.() -> Unit) {
        element(SpanH(), init)
    }

    fun thead(init: TheadH<MESSAGE>.() -> Unit) {
        element(TheadH(), init)
    }

    fun th(init: THH<MESSAGE>.() -> Unit) {
        element(THH(), init)
    }

    fun input(init: InputH<MESSAGE>.() -> Unit) {
        element(InputH(), init)
    }

    fun b(init: BH<MESSAGE>.() -> Unit) {
        element(BH(), init)
    }

    fun <ELEMENT: HTML<MESSAGE>> element(element: ELEMENT, init: ELEMENT.() -> Unit) {
        element.init()
        add(element)
    }

//    fun <CHILD_MESSAGE> add(html: HTML<CHILD_MESSAGE>, mapFun: (CHILD_MESSAGE) -> MESSAGE) {
//        val newMessageBus = MessageBusImpl<CHILD_MESSAGE> {message -> messageBus.send(mapFun.invoke(message))}
//        html.nullableMessageBus = newMessageBus
//        children.add(html.toH())
//    }
//
    open fun add(html: HTMLChild) {
        when(html) {
            is HTMLTextChild -> children.add(html)
            is HTML<*> -> {
                if (html.nullableMessageBus == null) {
                    html.nullableMessageBus = messageBus as MessageBus<Any?>
                }
                children.add(html)
            }
        }
    }

    fun <PARENT_MESSAGE> map(parent: HTML<PARENT_MESSAGE>, mapFun: (MESSAGE) -> PARENT_MESSAGE) {
        val newMessageBus = MessageBusImpl<MESSAGE> { message -> parent.messageBus.send(mapFun.invoke(message)) }
        nullableMessageBus = newMessageBus

//        parent.children.addAll(children)
        children.forEach { parent.add(it) }
    }

    operator fun String.unaryPlus() {
        children.add(HTMLTextChild(this))
    }

    var classes: String?
        set(value) {
            addAttribute("class", value)
        }
        get() = attrs["class"]

    var style: String?
        set(value) {
            addAttribute("style", value)
        }
        get() = attrs["style"]

    fun addAttribute(name: String, attr: dynamic) {
        attrs[name] = attr
//        if (data["attrs"] == null) {
//            data["attrs"] = object {}
//        }
//        data["attrs"][name] = attr
    }

    fun getAttribute(key: String) : dynamic =
        attrs[key]

    fun hasAttribute(key: String) = attrs.containsKey(key)

    fun <EVENT : Event> addHandler(name: String, handler: (EVENT) -> MESSAGE) {
        handlers[name] = { event : EVENT -> messageBus.send(handler(event)) }
//        if (data["on"] == null) {
//            data["on"] = object {}
//        }
//        data["on"][name] = { event -> messageBus.send(handler(event)) }
    }

    fun toStringDeep(indent: Int = 0): String {
        val sb = StringBuilder()

        sb.indent(indent).append(name).append(" {").append("\n")
        attrs.forEach { sb.indent(indent + 1).append(it.key).append("=").append(it.value.toString()).append("\n") }
        handlers.forEach { sb.indent(indent + 1).append("-> ").append(it.key).append("\n") }
        children.forEach {
            when(it) {
                is HTMLTextChild -> sb.indent(indent +1).append(it.text).append("\n")
                is HTML<*> -> sb.append(it.toStringDeep(indent + 1))
            }
        }
        sb.indent(indent).append("}\n")
        return sb.toString()
    }

    private fun StringBuilder.indent(indent: Int) : StringBuilder {
        this.append("  ".repeat(indent))
        return this
    }

    override fun toString(): String {
        val txt =
            if (text == null) {
                ""
            } else {
                ", text='$text'"
            }
        return "HTML(name='$name', attrs=$attrs, children.size=${children.size}$txt)"
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

class SpanH<MESSAGE> : HTML<MESSAGE>("span")

class DivH<MESSAGE> : HTML<MESSAGE>("div")

class TableH<MESSAGE> : HTML<MESSAGE>("table")

class TheadH<MESSAGE> : HTML<MESSAGE>("thead")

class THH<MESSAGE> : HTML<MESSAGE>("th")

class BH<MESSAGE> : HTML<MESSAGE>("b")

class TDH<MESSAGE> : HTML<MESSAGE>("td") {

    fun onClick(handler: (Event) -> MESSAGE) {
        addHandler("click", handler)
    }

}

class TRH<MESSAGE> : HTML<MESSAGE>("tr")

data class InputEvent(val value: String)

class InputH<MESSAGE> : HTML<MESSAGE>("input") {

    var value: String?
        set(value) {
            addAttribute("value", value)
        }
        get() = getAttribute("value")

    var autofocus: Boolean
        set(value) {
            if (value) {
                addAttribute("autofocus", "autofocus")
            }
        }
        get() = getAttribute("autofocus") == null

    // TODO enum?
    var type: String
        set(value) {
            addAttribute("type", value)
        }
        get() = (getAttribute("type") as String?) ?: "text"

    var min: Int?
        set(value) {
            if (value != null) {
                addAttribute("min", value.toString())
            }
        }
        get() = (getAttribute("min") as String?)?.toInt()

    var max: Int?
        set(value) {
            if (value != null) {
                addAttribute("max", value.toString())
            }
        }
        get() = (getAttribute("max") as String?)?.toInt()

    var step: Int?
        set(value) {
            if (value != null) {
                addAttribute("step", value.toString())
            }
        }
        get() = (getAttribute("step") as String?)?.toInt()

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

class ButtonH<MESSAGE> : HTML<MESSAGE>("button") {

    fun onClick(handler: (Event) -> MESSAGE) {
        addHandler("click", handler)
    }

}

interface HTMLRenderer {
    fun render(element: Element, htmlChild: HTMLChild)
}

interface HTMLChild

data class HTMLTextChild(val text: String) : HTMLChild
