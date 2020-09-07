package org.iuv.core.html.groups

import org.iuv.core.HTMLElement
import org.iuv.core.html.elements.Command
import org.iuv.core.html.elements.Script

interface CommonPhrasingElementsMetaDataElements<MESSAGE> : HTMLElement<MESSAGE>
 
 {
    fun command(init: Command<MESSAGE>.() -> Unit) {
        element(Command(), init)
    }
    fun script(init: Script<MESSAGE>.() -> Unit) {
        element(Script(), init)
    }
}