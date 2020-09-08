package org.iuv.core.html.elements
import org.iuv.core.HTML

open class PhrasingContentDel<MESSAGE> : HTML<MESSAGE>("del")
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