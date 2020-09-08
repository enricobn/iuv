package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Label<MESSAGE> : HTML<MESSAGE>("label")
 ,PhrasingContentElement<MESSAGE>
 
 
 {
    var for_: String?
        set(value) {
            if (value == null) {
                removeAttribute("for")
            } else {
                addAttribute("for", value)
            }
        }
        get() = (getAttribute("for"))
    var form: String?
        set(value) {
            if (value == null) {
                removeAttribute("form")
            } else {
                addAttribute("form", value)
            }
        }
        get() = (getAttribute("form"))


}