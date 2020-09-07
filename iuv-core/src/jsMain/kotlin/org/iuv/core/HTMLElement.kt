package org.iuv.core

@HtmlTagMarker
interface HTMLElement<MESSAGE> {

    fun add(html: HTMLChild)

    fun <ELEMENT: HTML<MESSAGE>> element(element: ELEMENT, init: ELEMENT.() -> Unit) {
        element.init()
        add(element)
    }

}