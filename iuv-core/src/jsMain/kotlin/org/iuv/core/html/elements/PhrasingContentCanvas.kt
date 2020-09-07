package org.iuv.core.html.elements
import org.iuv.core.HTML

open class PhrasingContentCanvas<MESSAGE> : HTML<MESSAGE>("canvas")
 ,PhrasingContentElement<MESSAGE>
 
 
 {
    var height: Int?
        set(value) {
            if (value == null) {
                removeProperty("height")
            } else {
                addProperty("height", value)
            }
        }
        get() = (getProperty("height"))

    var width: Int?
        set(value) {
            if (value == null) {
                removeProperty("width")
            } else {
                addProperty("width", value)
            }
        }
        get() = (getProperty("width"))



}