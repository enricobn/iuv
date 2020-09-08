package org.iuv.core.html.elements
import org.iuv.core.HTML

open class PhrasingContentIns<MESSAGE> : HTML<MESSAGE>("ins")
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