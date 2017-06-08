package org.enricobn.iuv.example

import org.enricobn.iuv.h
import org.enricobn.iuv.patch
import kotlin.browser.document

fun main(args: Array<String>) {
    val vnode = h("span", "foobar")
    val container = document.body

    patch(container!!, vnode)

    val newVnode = h("div", js("""[
    h('a', {on: {click: [clickHandler, 1]}, attrs: {href: '#'}}, '1'),
    h('a', {on: {click: [clickHandler, 2]}, attrs: {href: '#'}}, '2'),
    h('a', {on: {click: [clickHandler, 3]}, attrs: {href: '#'}}, '3'),
    ]"""))

    patch(vnode, newVnode);

//    val vu = TestIUV()
//
//    val loop = IUVLoop(vu)
//    loop.run()
}

fun clickHandler(number: Int) {
    console.log("button $number was clicked!");
}
