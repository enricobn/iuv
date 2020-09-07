package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface ObjectAttributeGroupVideoAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
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