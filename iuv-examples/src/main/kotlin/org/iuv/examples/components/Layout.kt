package org.iuv.examples.components

import org.iuv.core.HTML
import org.iuv.core.HTMLChild
import org.iuv.core.html.elements.Div

fun <MESSAGE> Div<MESSAGE>.vBox(init: Div<MESSAGE>.() -> Unit) {
    val html = object : Div<MESSAGE>() {
        override fun add(html: HTMLChild) {
            val wrapper = HTML<MESSAGE>("div")
            wrapper.add(html)
            super.add(wrapper)
        }
    }

    init(html)

    add(html)

//            // TODO I'm loosing all attributes and handlers set in the init function!
//
//            div {
//                html.getChildren().forEach {
//                    div {
//                        add(it)
//                    }
//                }
//            }
}
