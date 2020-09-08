package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Embed<MESSAGE> : HTML<MESSAGE>("embed")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var src: String?
        set(value) {
            if (value == null) {
                removeAttribute("src")
            } else {
                addAttribute("src", value)
            }
        }
        get() = (getAttribute("src"))
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