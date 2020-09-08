package org.iuv.core.html.elements
import org.iuv.core.HTML

open class FlowContentCanvas<MESSAGE> : HTML<MESSAGE>("canvas")
 ,FlowContentElement<MESSAGE>
 
 
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