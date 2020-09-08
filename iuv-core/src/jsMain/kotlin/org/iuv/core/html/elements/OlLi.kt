package org.iuv.core.html.elements
import org.iuv.core.HTML

open class OlLi<MESSAGE> : HTML<MESSAGE>("li")
 ,Li<MESSAGE>
 
 
 {
    var value: Int?
        set(value) {
            if (value == null) {
                removeAttribute("value")
            } else {
                addAttribute("value", value)
            }
        }
        get() = (getAttribute("value"))


}