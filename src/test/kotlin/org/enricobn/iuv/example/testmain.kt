package org.enricobn.iuv.example

import org.enricobn.iuv.IUVApplication
import org.enricobn.iuv.h

fun main(args: Array<String>) {
//    val vnode = h("span", "foobar")
//    val container = document.body
//
//    patch(container!!, vnode)
//
//    val newVnode = h("div", arrayOf(a(1, ::clickHandler), a(2, ::clickHandler), a(3, ::clickHandler)))
//
//    patch(vnode, newVnode)

    val application = IUVApplication(TestIUV("IT"))
    application.run()
}


private fun a(number: Int, handler: (Int) -> Unit) : dynamic {
    val o : dynamic = object {}

    newObject(o, "on")["click"] = arrayOf(handler, number)
    newObject(o, "attrs")["href"] = "#"

//    val o1: dynamic = object {
//        val on =
//                object {
//                    val click = arrayOf(handler, number)
//                }
//        val attrs = object {
//            val href = "#"
//        }
//    }
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