package org.iuv.core

import org.w3c.dom.Element

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

class SnabbdomRenderer : HTMLRenderer {
    private var viewH : dynamic = null
    private val onFirstPatchHandlers = mutableListOf<() -> Unit>()
    private val onSubsequentPatchHandlers = mutableListOf<() -> Unit>()

    fun onFirstPatch(handler: () -> Unit) {
        onFirstPatchHandlers += handler
    }

    fun onSubsequentPatch(handler: () -> Unit) {
        onSubsequentPatchHandlers += handler
    }

    companion object {
        private val patch: (old: dynamic, new: dynamic) -> Unit = snabbdomInit()
    }

    override fun render(element: Element, htmlChild: HTMLChild) {
        val newH = toH(htmlChild)

        if (viewH == null) {
            patch(element, newH)
            onFirstPatchHandlers.forEach { it.invoke() }
            getJsToRun(htmlChild).forEach { eval(it) }
        } else {
            patch(viewH, newH)
            onSubsequentPatchHandlers.forEach { it.invoke() }
            getJsToRun(htmlChild).forEach { eval(it) }
        }

        viewH = newH

    }

    private fun getJsToRun(htmlChild: HTMLChild) : List<String> =
        when (htmlChild) {
            is HTML<*> -> {
                val allJs = htmlChild.getJsTorRun().toMutableList()
                allJs.addAll(htmlChild.getChildren().flatMap { getJsToRun(it) })
                allJs
            }
            else ->  emptyList()
        }

    private fun toH(htmlChild: HTMLChild): dynamic =
            when (htmlChild) {
                is HTML<*> ->
                    if (htmlChild.getText() != null) {
                        snabbdom.h(htmlChild.name, getData(htmlChild), htmlChild.getText())
                    } else {
                        val renderedChildren = htmlChild.getChildren().map { toH(it) }

                        // is this faster?
//                    val renderedChildren = htmlData.children.map { child ->
//                        when (child) {
//                            is HTMLElementData -> render(child)
//                            is HTMLTextData -> child.text
//                            else -> throw IllegalStateException()
//                        }
//                    }

                        snabbdom.h(htmlChild.name, getData(htmlChild), renderedChildren.toTypedArray())
                    }
                is HTMLTextChild -> htmlChild.text
                else -> throw IllegalStateException()
            }


    private fun getData(html: HTML<*>) : dynamic {
        val data: dynamic = object {}

        val dynAttrs: dynamic

        if (!html.getAttrs().isEmpty()) {
            dynAttrs = object {}
            data["attrs"] = dynAttrs
            html.getAttrs().forEach { (key, value) -> dynAttrs[key] = value }
        }

        val on: dynamic

        if (!html.getHandlers().isEmpty()) {
            on = object {}
            data["on"] = on

            html.getHandlers().forEach { (key, value) -> on[key] = value }
        }

        return data
    }
}