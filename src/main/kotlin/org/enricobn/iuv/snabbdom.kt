package org.enricobn.iuv

import org.w3c.dom.events.Event


external fun h(sel: String, a: dynamic, b: dynamic) : dynamic = definedExternally
external fun h(sel: String, a: dynamic) : dynamic = definedExternally
external fun h(sel: String) : dynamic = definedExternally

external fun patch(old: dynamic, new: dynamic) : Unit = definedExternally

@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class HTML(val name: String) {
    protected val data : dynamic = object {}
    val children = mutableListOf<dynamic>()
    protected var text : String? = null

    fun div(init: DivH.() -> Unit) {
        val html = DivH()
        html.init()
        children.add(html.toH())
    }

    fun td(init: TDH.() -> Unit) {
        val element = TDH()
        element.init()
        children.add(element.toH())
    }

    fun tr(init: TRH.() -> Unit) {
        val element = TRH()
        element.init()
        children.add(element.toH())
    }

    fun table(init: TableH.() -> Unit) {
        val element = TableH()
        element.init()
        children.add(element.toH())
    }

    fun button(init: ButtonH.() -> Unit) {
        val element = ButtonH()
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

    fun addHandler(name: String, handler: (Event) -> dynamic) {
        if (data["on"] == null) {
            data["on"] = object {}
        }
        data["on"][name] = handler
    }

}

class DivH : HTML("div")

class TableH : HTML("table")

class TDH : HTML("td")

class TRH : HTML("tr")

class ButtonH : HTML("button") {

    fun onClick(handler: (Event) -> dynamic) : Unit {
        addHandler("click", handler)
    }

    var classes: String? = null
        set(value) {
            addAttr("class", value)
        }
}

