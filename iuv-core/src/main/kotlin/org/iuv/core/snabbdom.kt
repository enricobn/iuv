package org.iuv.core

import org.w3c.dom.Element
import kotlin.js.Date

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
        private fun checkedRun(runnable: () -> Unit) {
            // TODO stacktrace
            try {runnable.invoke()}catch(e:Throwable){ console.error("Error in patch: ${e.message}") }
        }
    }

    override fun <MESSAGE> render(element: Element, html: HTML<MESSAGE>) {
        var time = Date().getTime()
        val newH = toH(html)

        console.log("toH ${Date().getTime() - time}")

        time = Date().getTime()

        if (viewH == null) {
            patch(element, newH)
            onFirstPatchHandlers.forEach { checkedRun(it) }
            getJsToRun(html).forEach { checkedRun { eval(it) } }
        } else {
            patch(viewH, newH)
            onSubsequentPatchHandlers.forEach { checkedRun(it) }
            getJsToRun(html).forEach { checkedRun { eval(it) } }
        }

        console.log("patch ${Date().getTime() - time}")

        viewH = newH

    }

    // TODO optimize
    private fun getJsToRun(htmlChild: HTMLChild) : List<String> =
        when (htmlChild) {
            is HTML<*> -> {
                val allJs = htmlChild.getJsTorRun().toMutableList()
                allJs.addAll(htmlChild.children.flatMap { getJsToRun(it) })
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
                        val renderedChildren = htmlChild.children.map { toH(it) }

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
        val data : dynamic = object {}
        data.props = html.props
        data.attrs = html.attrs
        data.on = html.handlers
        return data
    }

//    private fun getData(html: HTML<*>) : dynamic {
//        val data: dynamic = object {}
//
//        if (!html.getAttrs().isEmpty()) {
//            val dynAttrs: dynamic = object {}
//            data["attrs"] = dynAttrs
//            html.getAttrs().forEach { (key, value) -> dynAttrs[key] = value }
//        }
//
//        if (!html.getProps().isEmpty()) {
//            val dynProps: dynamic = object {}
//            data["props"] = dynProps
//            html.getProps().forEach { (key, value) -> dynProps[key] = value }
//
//            // it seems that snabbdom does not handle correctly value so we force the update
//            // https://github.com/snabbdom/snabbdom/issues/53
//            if (html.getProps().containsKey("value")) {
//                val hook = js("({})")
//                data["hook"] = hook
//
//                hook["update"] = this::hookToUpdateValue
//            }
//        }
//
//        // key is a special value for snabbdom, it's used to distinguish vtrees
//        val key = html.getAttrs()["key"]
//
//        if (key != null) {
//            data["key"] = key
//        }
//
//        if (!html.getHandlers().isEmpty()) {
//            val on: dynamic = object {}
//            data["on"] = on
//
//            html.getHandlers().forEach { (key, value) -> on[key] = value }
//        }
//
//        return data
//    }

    private fun hookToUpdateValue(oldVnode : dynamic, vnode : dynamic ) {
        vnode.elm.value = vnode.data.props.value
    }
}