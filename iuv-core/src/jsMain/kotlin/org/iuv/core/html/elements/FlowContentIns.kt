package org.iuv.core.html.elements
import org.iuv.core.HTML

open class FlowContentIns<MESSAGE> : HTML<MESSAGE>("ins")
 ,FlowContentElement<MESSAGE>
 
 
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