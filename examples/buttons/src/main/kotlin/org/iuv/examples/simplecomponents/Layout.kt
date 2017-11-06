package org.iuv.examples.simplecomponents

import org.iuv.core.HTML
import org.iuv.core.HTMLChild

fun <MESSAGE> HTML<MESSAGE>.vBox(init: HTML<MESSAGE>.() -> Unit) {
    val html = object : HTML<MESSAGE>("div") {
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
