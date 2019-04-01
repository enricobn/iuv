package org.iuv.core

import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.InputEvent
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window

@DslMarker
annotation class HtmlTagMarker
@HtmlTagMarker
open class HTML<MESSAGE>(val name: String) : HTMLChild {
    internal val attrs: dynamic = object {}
    internal val children = mutableListOf<HTMLChild>()
    private val jsToRun = mutableListOf<String>()
    internal var props: dynamic = null
    internal var handlers: dynamic = null
    private var text : String? = null
    private var mapFun : ((Any) -> Any)? = null
    private var parent : HTML<Any>? = null

    private fun messageBus() : MessageBus<MESSAGE> { return IUVGlobals.getMessageBus() as MessageBus<MESSAGE>
    }

    fun getChildren() = children.toList()

    fun getAttrs() : dynamic {
        return attrs
    }

    fun getProps(): dynamic {
        return props
    }

    fun getHandlers() : dynamic {
        return handlers
    }

    fun hasHandler(name: String) = getHandler(name) == null

    fun getHandler(name: String) : ((Event) -> MESSAGE)? {
        return if (handlers == null) null else handlers[name] as ((Event) -> MESSAGE)
    }

    fun getText() = text

    fun getJsTorRun() = jsToRun.toList()

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

    fun tbody(init: TbodyH<MESSAGE>.() -> Unit) {
        element(TbodyH(), init)
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

    fun label(init: LabelH<MESSAGE>.() -> Unit) {
        element(LabelH(), init)
    }

    fun ul(init: UlH<MESSAGE>.() -> Unit) {
        element(UlH(), init)
    }

    fun li(init: LiH<MESSAGE>.() -> Unit) {
        element(LiH(), init)
    }

    fun header(init: HeaderH<MESSAGE>.() -> Unit) {
        element(HeaderH(), init)
    }

    fun nav(init: NavH<MESSAGE>.() -> Unit) {
        element(NavH(), init)
    }

    fun a(init: AH<MESSAGE>.() -> Unit) {
        element(AH(), init)
    }

    fun main(init: MainH<MESSAGE>.() -> Unit) {
        element(MainH(), init)
    }

    fun br() {
        add(HTML<MESSAGE>("br"))
    }

    fun checkbox(init: CheckBoxH<MESSAGE>.() -> Unit) {
        element(CheckBoxH(), init)
    }

    fun img(init: IMAGEH<MESSAGE>.() -> Unit) {
        element(IMAGEH(), init)
    }

    protected fun <ELEMENT: HTML<MESSAGE>> element(element: ELEMENT, init: ELEMENT.() -> Unit) {
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
                html.parent = this as HTML<Any>
                children.add(html)
            }
        }
    }

    fun <CHILD_MESSAGE> add(html: HTML<CHILD_MESSAGE>, mapFun: (CHILD_MESSAGE) -> MESSAGE) {
        html.children.forEach {
            add(it)
            if (it is HTML<*>) {
                if (it.mapFun == null) {
                    it.mapFun = mapFun as (Any) -> Any
                } else {
                    val oldMapFun = it.mapFun
                    it.mapFun = { msg -> mapFun.invoke(oldMapFun?.invoke(msg) as CHILD_MESSAGE) as Any}
                }
            }
        }
    }

    fun <MODEL,CHILD_MODEL,CHILD_MESSAGE> add(childComponent: ChildComponent<MODEL,MESSAGE,CHILD_MODEL,CHILD_MESSAGE>, model: MODEL) {
        childComponent.addTo(this, model)
    }

    /**
     * TODO experimental
     */
    fun runJs(code: String) {
        jsToRun.add(code)
    }

    operator fun String.unaryPlus() {
        children.add(HTMLTextChild(this))
    }

    var classes: String?
        set(value) {
            addAttribute("class", value)
        }
        get() = getAttribute("class")

    fun appendClasses(vararg classesToAppend: String) {
        val allClasses =
                classes.let {
                    if (it != null && it.isNotEmpty()) {
                        it + " "
                    } else {
                        ""
                    }
                } + classesToAppend.joinToString(" ")
        classes = allClasses
    }

    var style: String?
        set(value) {
            addAttribute("style", value)
        }
        get() = getAttribute("style") as String?

    var id: String?
        set(value) {
            addAttribute("id", value)
        }
        get() = getAttribute("id") as String?

    var key: String?
        set(value) {
            addAttribute("key", value)
        }
        get() = getAttribute("key") as String?

    fun addAttribute(name: String, attr: dynamic) {
        attrs[name] = attr
    }

    fun removeAttribute(name: String) {
        deleteProperty(attrs, name)
    }

    fun getAttribute(key: String) : dynamic {
        return if (attrs == null) null else attrs[key]
    }

    fun hasAttribute(key: String) : Boolean {
        return getAttribute(key) == null
    }

    fun addProperty(name: String, prop: dynamic) {
        if (props == null) {
            props = object {}
        }
        props[name] = prop
    }

    fun removeProperty(name: String) {
        deleteProperty(props, name)
    }

    fun getProperty(key: String) : dynamic {
        return if (props == null) props else props[key]
    }

    fun hasProperty(key: String) : Boolean {
        return getProperty(key) == null
    }

    fun <EVENT : Event> on(name: String, handler: (EVENT) -> MESSAGE) {
        if (handlers == null) {
            handlers = object {}
        }
        handlers[name] = { event : EVENT ->
            var msg = handler(event) as Any

            var p : HTML<Any>? = this as HTML<Any>

            while (p != null) {

                p.mapFun?.let {
                    msg = it.invoke(msg)
                }

                p = p.parent
            }

            messageBus().send(msg)
        }
    }

    fun toStringDeep(indent: Int = 0): String {
        val sb = StringBuilder()
//
//        sb.indent(indent).append(name).append(" {").append("\n")
//        attrs.forEach { sb.indent(indent + 1).append(it.key).append("=").append(it.value.toString()).append("\n") }
//        handlers.forEach { sb.indent(indent + 1).append("-> ").append(it.key).append("\n") }
//        children.forEach {
//            when(it) {
//                is HTMLTextChild -> sb.indent(indent +1).append(it.text).append("\n")
//                is HTML<*> -> sb.append(it.toStringDeep(indent + 1))
//            }
//        }
//        sb.indent(indent).append("}\n")
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
        return "HTML(name='$name', attrs=${getAttrs()}, children.size=${children.size}$txt)"
    }

//    fun <CHILD_MODEL,CHILD_MESSAGE> childView(uv: Component<CHILD_MODEL, CHILD_MESSAGE>,
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
//         * but it's different from what happens in the top level Component (View).
//         */
//        children.addAll(result.children)
//
//        // I don't throw away the div!
//        // children.add(result.toH())
//    }

}


class SpanH<MESSAGE> : HTML<MESSAGE>("span"),ClickableHTML<MESSAGE>

class DivH<MESSAGE> : HTML<MESSAGE>("div")

class TableH<MESSAGE> : HTML<MESSAGE>("table")

class TheadH<MESSAGE> : HTML<MESSAGE>("thead")

class TbodyH<MESSAGE> : HTML<MESSAGE>("tbody")

class THH<MESSAGE> : HTML<MESSAGE>("th")

class BH<MESSAGE> : HTML<MESSAGE>("b")

class TDH<MESSAGE> : HTML<MESSAGE>("td"),ClickableHTML<MESSAGE>

class TRH<MESSAGE> : HTML<MESSAGE>("tr"),ClickableHTML<MESSAGE>

open class InputH<MESSAGE> : HTML<MESSAGE>("input"),ClickableHTML<MESSAGE> {

    var value: String?
        set(value) {
            if (value == null) {
                removeProperty("value")
            } else {
                addProperty("value", value)
            }
        }
        get() = getProperty("value")

    var defaultValue: String?
        set(value) {
            if (value == null) {
                removeAttribute("defaultValue")
            } else {
                addAttribute("defaultValue", value)
            }
        }
        get() = getProperty("defaultValue")


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

    var placeholder: String?
        set(value) {
            if (value != null) {
                addAttribute("placeholder", value.toString())
            }
        }
        get() = (getAttribute("placeholder") as String?)

    fun onInput(handler: (InputEvent,String) -> MESSAGE) {
        on("input", { event: InputEvent ->
            handler(event, event.target?.asDynamic().value)
        })
    }

    fun onChange(handler: (InputEvent,String) -> MESSAGE) {
        on("change", { event: InputEvent ->
            handler(event, event.target?.asDynamic().value)
        })
    }

    fun onInput(message: MESSAGE) {
        on("input", { _: InputEvent -> message })
    }

    fun onChange(message: MESSAGE) {
        on("change", { _: InputEvent -> message })
    }

    fun onBlur(handler: (InputEvent,String) -> MESSAGE) {
        on("blur", { event: InputEvent ->
            handler(event, event.target?.asDynamic().value)
        })
    }

    fun onBlur(message: MESSAGE) {
        on("blur", { _: InputEvent -> message })
    }

    fun onKeydown(handler: (KeyboardEvent, String) -> MESSAGE) {
        on("keydown", { event: KeyboardEvent ->
            handler(event, event.target?.asDynamic().value)
        })
    }

    fun onFocus(handler: (InputEvent, String) -> MESSAGE) {
        on("focus", { event: InputEvent ->
            handler(event, event.target?.asDynamic().value)
        })
    }

    fun onFocus(message: MESSAGE) {
        on("focus") { _: InputEvent -> message }
    }
}

class CheckBoxH<MESSAGE> : InputH<MESSAGE>() {

    init {
        type = "checkbox"
    }

    var checked: Boolean
        set(value) {
//                addAttribute("value", value)
            addProperty("checked", value)
        }
//        get() = getAttribute("value")
        get() = getProperty("checked") ?: false

    fun onChecked(handler: (Event, Boolean) -> MESSAGE) {
        val clickHandler: (Event) -> MESSAGE = {
            val checked : Boolean = (it.target?.asDynamic()).checked

            handler(it, checked)
        }
        on("click", clickHandler)
    }

}

open class ButtonH<MESSAGE> : HTML<MESSAGE>("button"),ClickableHTML<MESSAGE>

class LabelH<MESSAGE> : HTML<MESSAGE>("label") {

    var forElement: String?
        set(value) {
            addAttribute("for", value)
        }
        get() = getAttribute("for") as String?
}

class UlH<MESSAGE> : HTML<MESSAGE>("ul")

class LiH<MESSAGE> : HTML<MESSAGE>("li")

class HeaderH<MESSAGE> : HTML<MESSAGE>("header")

class NavH<MESSAGE> : HTML<MESSAGE>("nav")

class AH<MESSAGE> : HTML<MESSAGE>("a"),ClickableHTML<MESSAGE> {
    var href: String?
        set(value) {
            addAttribute("href", value)
        }
        get() = getAttribute("href") as String?

    fun navigate(path: String) {
        if (path.startsWith("/")) {
            addAttribute("href", "#" + path)
        } else {
            addAttribute("href", "#" + window.location.hash + "/" + path)
        }
    }

}

class IMAGEH<MESSAGE> : HTML<MESSAGE>("img") {
    var src: String?
        set(value) {
            addAttribute("src", value)
        }
        get() = getAttribute("src") as String?

}

class MainH<MESSAGE> : HTML<MESSAGE>("main")

interface HTMLRenderer {
    fun <MESSAGE> render(element: Element, html: HTML<MESSAGE>)
}

interface OnHTMLEvents<in MESSAGE> {
    fun <EVENT : Event> on(name: String, handler: (EVENT) -> MESSAGE)
}

interface ClickableHTML<in MESSAGE> : OnHTMLEvents<MESSAGE> {

    fun onClick(handler: (Event) -> MESSAGE) {
        on("click", handler)
    }

    fun onClick(message: MESSAGE) {
        on("click") { _ : Event -> message }
    }
}

interface DataAttributesHTML<MESSAGE> : WithAttributesHTML<MESSAGE> {

    var dataToggle: String?
        set(value) {
            if (value != null) {
                addAttribute("data-toggle", value.toString())
            }
        }
        get() = getAttribute("data-toggle") as String?

    var dataTarget: String?
        set(value) {
            if (value != null) {
                addAttribute("data-target", value.toString())
            }
        }
        get() = getAttribute("data-target") as String?
}

interface WithAttributesHTML<MESSAGE> {
    fun addAttribute(name: String, attr: dynamic)

    fun removeAttribute(name: String)

    fun getAttribute(key: String) : dynamic
}


interface HTMLChild

data class HTMLTextChild(val text: String) : HTMLChild

// TODO what's this?
fun deleteProperty(obj: Any, property: Any) {
    js("delete obj[property]")
}