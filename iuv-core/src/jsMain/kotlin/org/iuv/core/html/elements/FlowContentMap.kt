package org.iuv.core.html.elements
import org.iuv.core.HTML

open class FlowContentMap<MESSAGE> : HTML<MESSAGE>("map")
 ,FlowContentElement<MESSAGE>
 
 
 {
    var name: String?
        set(value) {
            if (value == null) {
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))


}