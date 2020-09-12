package org.iuv.core

@HtmlTagMarker
interface HTMLElement<MESSAGE> {

    fun add(html: HTMLChild)

    fun <ELEMENT: HTML<MESSAGE>> element(element: ELEMENT, init: ELEMENT.() -> Unit) {
        element.init()
        add(element)
    }

    fun <MODEL,CHILD_MODEL,CHILD_MESSAGE> add(childComponent: ChildComponent<MODEL,MESSAGE,CHILD_MODEL,CHILD_MESSAGE>, model: MODEL)

    operator fun String.unaryPlus()

}