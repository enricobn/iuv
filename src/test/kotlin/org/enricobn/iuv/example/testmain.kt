package org.enricobn.iuv.example

import org.enricobn.iuv.h
import org.enricobn.iuv.patch
import kotlin.browser.document
import kotlin.js.json

fun main(args: Array<String>) {
    val vnode = h("span", "foobar")
    val container = document.body

    patch(container!!, vnode)

//    val newVnode = h("div", js("""[
//        h('a', {on: {click: [clickHandler, 1]}, attrs: {href: '#'}}, '1'),
//        h('a', {on: {click: [clickHandler, 2]}, attrs: {href: '#'}}, '2'),
//        h('a', {on: {click: [clickHandler, 3]}, attrs: {href: '#'}}, '3'),
//        ]"""))

    val newVnode = h("div", arrayOf(a(1, ::clickHandler), a(2, ::clickHandler), a(3, ::clickHandler)))

    patch(vnode, newVnode)

    val j = json(
            Pair("on", json(
                        Pair("click", arrayOf(::clickHandler, 1))
                       )
            ),
            Pair("attrs", json(Pair("href", "#")))
    )

    var o : dynamic = object {}

    o["pippo"]["caio"] = "pluto"


//    val vu = TestIUV()
//
//    val loop = IUVLoop(vu)
//    loop.run()
}


private fun a(number: Int, handler: (Int) -> Unit) : dynamic {
    var o : dynamic = object {}

    newObject(o, "on")["click"] = arrayOf(handler, number)
    newObject(o, "attrs")["href"] = "#"

    val o1: dynamic = object {
        val on =
                object {
                    val click = arrayOf(handler, number)
                }
        val attrs = object {
            val href = "#"
        }
    }
    return h("a", o, number.toString())
}

private fun newObject(d: dynamic, name: String) : dynamic {
    val result : dynamic = object {}
    d[name] = result
    return result
}

private fun clickHandler(number: Int) {
    console.log("button $number was clicked!")
}
