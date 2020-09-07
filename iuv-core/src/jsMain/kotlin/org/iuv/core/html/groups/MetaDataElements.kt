package org.iuv.core.html.groups

import org.iuv.core.HTMLElement
import org.iuv.core.html.elements.Link
import org.iuv.core.html.elements.Meta
import org.iuv.core.html.elements.Style

interface MetaDataElements<MESSAGE> : HTMLElement<MESSAGE>
 ,CommonPhrasingElementsMetaDataElements<MESSAGE>
 {
    fun link(init: Link<MESSAGE>.() -> Unit) {
        element(Link(), init)
    }
    fun style(init: Style<MESSAGE>.() -> Unit) {
        element(Style(), init)
    }
    fun meta(init: Meta<MESSAGE>.() -> Unit) {
        element(Meta(), init)
    }
}