package org.iuv.core.html.elements

class PhrasingContentCanvas<MESSAGE> : org.iuv.core.HTML<MESSAGE>("canvas")
 
 
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