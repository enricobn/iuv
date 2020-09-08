package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Q<MESSAGE> : HTML<MESSAGE>("q")
 ,PhrasingContentElement<MESSAGE>
 
 
 {
    var cite: String?
        set(value) {
            if (value == null) {
                removeAttribute("cite")
            } else {
                addAttribute("cite", value)
            }
        }
        get() = (getAttribute("cite"))


}