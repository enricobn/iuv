package org.iuv.core

import kotlinx.browser.window
import org.iuv.core.html.attributegroups.AAttributeGroup
import org.iuv.core.html.attributegroups.CoreAttributeGroupNodir
import org.w3c.dom.Element
import org.w3c.dom.events.Event

val keys = js("Object.keys")

@DslMarker
annotation class HtmlTagMarker
open class HTML<MESSAGE>(val elementName: String) : HTMLChild, HTMLElement<MESSAGE>, HTMLElementAttributes<MESSAGE> {
    internal val attrs: dynamic = object {}
    internal val children = mutableListOf<HTMLChild>()
    private val jsToRun = mutableListOf<String>()
    internal var props: dynamic = null
    internal var handlers: dynamic = null
    private var text : String? = null
    private var mapFun : ((Any) -> Any)? = null
    private var parent : HTML<Any>? = null
    internal var onElementDestroy : ((Element) -> Unit)? = null
    internal var onElementInsert : ((Element) -> Unit)? = null

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

    /*
    fun <ELEMENT: HTML<MESSAGE>> element(element: ELEMENT, classes: String?, init: ELEMENT.() -> Unit) {
        if (classes != null) {
            element.addClasses(classes)
        }
        element.init()
        add(element)
    }

     */

    //    fun <CHILD_MESSAGE> add(html: HTML<CHILD_MESSAGE>, mapFun: (CHILD_MESSAGE) -> MESSAGE) {
//        val newMessageBus = MessageBusImpl<CHILD_MESSAGE> {message -> messageBus.send(mapFun.invoke(message))}
//        html.nullableMessageBus = newMessageBus
//        children.add(html.toH())
//    }
//
    override fun add(html: HTMLChild) {
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

        for (attr in keys(html.attrs)) {
            attrs[attr] = html.attrs[attr]
        }

        if (html.props != null) {
            if (props == null)
                props = object {}
            for (attr in keys(html.props)) {
                props[attr] = html.props[attr]
            }
        }

        if (html.handlers != null) {
            if (handlers == null)
                handlers = object {}
            for (attr in keys(html.handlers)) {
                handlers[attr] = html.handlers[attr]
            }
        }

        jsToRun.addAll(html.jsToRun)

        onElementInsert = html.onElementInsert

        onElementDestroy = html.onElementDestroy

        text = html.text

    }

    fun addObject(source: dynamic, dest: dynamic) {

    }

    override fun <MODEL,CHILD_MODEL,CHILD_MESSAGE> add(childComponent: ChildComponent<MODEL,MESSAGE,CHILD_MODEL,CHILD_MESSAGE>, model: MODEL) {
        childComponent.addTo(this, model)
    }

    /**
     * TODO experimental
     */
    fun runJs(code: String) {
        jsToRun.add(code)
    }

    override operator fun String.unaryPlus() {
        children.add(HTMLTextChild(this))
    }

    /*
    /**
     * classes are separated by spaces
     */
    fun addClasses(classesToAdd: String) {
        classes = if (classes.isNullOrEmpty()) classesToAdd else "$classes $classesToAdd"
    }

     */

    override fun addAttribute(name: String, attr: dynamic) {
        attrs[name] = attr
    }

    override fun removeAttribute(name: String) {
        deleteProperty(attrs, name)
    }

    override fun getAttribute(key: String) : dynamic {
        return if (attrs == null) null else attrs[key]
    }

    override fun hasAttribute(key: String) : Boolean {
        return getAttribute(key) == null
    }

    override fun addProperty(name: String, prop: dynamic) {
        if (props == null) {
            props = object {}
        }
        props[name] = prop
    }

    override fun removeProperty(name: String) {
        deleteProperty(props, name)
    }

    override fun getProperty(key: String) : dynamic {
        return if (props == null) props else props[key]
    }

    override fun hasProperty(key: String) : Boolean {
        return getProperty(key) == null
    }

    override fun <EVENT : Event> on(name: String, handler: (EVENT) -> MESSAGE?) {
        if (handlers == null) {
            handlers = object {}
        }
        handlers[name] = { event : EVENT ->
            val msg = handler(event) as Any?

            if (msg != null) {
                val html: HTML<Any>? = this as HTML<Any>

                messageBus().send(mapMessage(msg, html))
            }
        }
    }

    override fun <EVENT : Event> on(name: String, handler: (EVENT, MessageBus<MESSAGE>) -> Unit) {
        if (handlers == null) {
            handlers = object {}
        }
        handlers[name] = { event : EVENT ->
            val html : HTML<Any>? = this as HTML<Any>

            handler(event, object: MessageBus<MESSAGE> {
                override fun send(message: MESSAGE) {

                    val msg : Any = message as Any

                    messageBus().send(mapMessage(msg, html))
                }

            })
        }
    }

    private fun mapMessage(message: Any, h: HTML<Any>?) : Any {
        var html = h
        var msg = message

        while (html != null) {

            html.mapFun?.let {
                msg = it.invoke(msg)
            }

            html = html.parent
        }
        return msg
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

    /**
     * called when an element is going to be destroyed, can be used to make some cleanup
     */
    fun onElementDestroy(fn: (Element) -> Unit) {
        this.onElementDestroy = fn
    }

    /**
     * called when an element is inserted in the DOM, can be used to make some specific initialization
     */
    fun onElementInsert(fn: (Element) -> Unit) {
        this.onElementInsert = fn
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
        return "HTML(name='$elementName', attrs=${getAttrs()}, children.size=${children.size}$txt)"
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

interface HTMLRenderer {
    fun <MESSAGE> render(element: Element, html: HTML<MESSAGE>)
}

interface HTMLChild

data class HTMLTextChild(val text: String) : HTMLChild

// TODO what's this?
fun deleteProperty(obj: Any, property: Any) {
    js("delete obj[property]")
}

fun <MESSAGE> AAttributeGroup<MESSAGE>.navigate(path: String) {
    if (path.startsWith("/")) {
        addAttribute("href", "#$path")
    } else {
        addAttribute("href", "#" + window.location.hash + "/" + path)
    }
}

fun <MESSAGE> CoreAttributeGroupNodir<MESSAGE>.appendClasses(vararg classesToAdd: String) {
    classes = ((classes ?: "") + classesToAdd.joinToString(separator = "") { " $it" }).trim()
}