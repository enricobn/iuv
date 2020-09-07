package org.iuv.core.html.elements
import org.iuv.core.HTML

open class PhrasingContentMap<MESSAGE> : HTML<MESSAGE>("map")
 ,PhrasingContentElement<MESSAGE>
 
 
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