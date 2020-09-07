package org.iuv.core.html.elements
import org.iuv.core.HTMLChild
import org.iuv.core.HTMLElement
import org.iuv.core.HTMLElementAttributes

interface InsFlow<MESSAGE> : HTMLChild, HTMLElement<MESSAGE>, HTMLElementAttributes<MESSAGE>
 
 
 
 {
    var cite: String?
        set(value) {
            if (value == null) {
                removeProperty("cite")
            } else {
                addProperty("cite", value)
            }
        }
        get() = (getProperty("cite"))



}