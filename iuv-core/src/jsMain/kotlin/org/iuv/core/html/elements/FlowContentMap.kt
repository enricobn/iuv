package org.iuv.core.html.elements
import org.iuv.core.HTML

open class FlowContentMap<MESSAGE> : HTML<MESSAGE>("map")
 ,FlowContentElement<MESSAGE>
 
 
 {
    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))



}