package org.iuv.core.html.groups

import org.iuv.core.HTMLElement
import org.iuv.core.html.elements.*

interface MetaDataElements<MESSAGE> : HTMLElement<MESSAGE>
 
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
    fun script(init: Script<MESSAGE>.() -> Unit) {
        element(Script(), init)
    }
    fun command(init: Command<MESSAGE>.() -> Unit) {
        element(Command(), init)
    }
}