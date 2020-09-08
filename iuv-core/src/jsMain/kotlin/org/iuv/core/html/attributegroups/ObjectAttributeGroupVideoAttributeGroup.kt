package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface ObjectAttributeGroupVideoAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var height: Int?
        set(value) {
            if (value == null) {
                removeAttribute("height")
            } else {
                addAttribute("height", value)
            }
        }
        get() = (getAttribute("height"))
    var width: Int?
        set(value) {
            if (value == null) {
                removeAttribute("width")
            } else {
                addAttribute("width", value)
            }
        }
        get() = (getAttribute("width"))

}