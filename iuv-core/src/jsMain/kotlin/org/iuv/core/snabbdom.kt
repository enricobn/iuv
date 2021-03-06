package org.iuv.core

import kotlinext.js.require
import org.w3c.dom.Element
import kotlin.js.Date

var snabbdom_: dynamic = require("snabbdom/snabbdom.js")

object snabbdom {

    fun init(props: dynamic) : dynamic = snabbdom_.init(props)

    fun h(sel: String, a: dynamic, b: dynamic) : dynamic = snabbdom_.h(sel, a, b)

}

var snabbdom_style: dynamic = require("snabbdom/modules/style.js")
var snabbdom_class: dynamic = require("snabbdom/modules/class.js")
var snabbdom_props: dynamic = require("snabbdom/modules/props.js")
var snabbdom_attributes: dynamic = require("snabbdom/modules/attributes.js")
var snabbdom_eventlisteners: dynamic = require("snabbdom/modules/eventlisteners.js")

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

        if (printTime)
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

        if (printTime)
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
                        snabbdom.h(htmlChild.elementName, getData(htmlChild), htmlChild.getText())
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

                        snabbdom.h(htmlChild.elementName, getData(htmlChild), renderedChildren.toTypedArray())
                    }
                is HTMLTextChild -> htmlChild.text
                else -> error("Invalid child")
            }

    private fun getData(html: HTML<*>) : dynamic {
        val data : dynamic = object {}
        data.props = html.props
        data.attrs = html.attrs
        data.on = html.handlers

        val onElementDestroy = html.onElementDestroy
        val onElementInsert = html.onElementInsert

        if (onElementDestroy != null || onElementInsert != null) {
            data.hook = object {}
        }

        if (onElementDestroy != null) {
            data.hook.destroy = { vnode: dynamic ->
                onElementDestroy.invoke(vnode.elm)
            }
        }

        if (onElementInsert != null) {
            data.hook.insert = { vnode: dynamic ->
                onElementInsert.invoke(vnode.elm)
            }
        }

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