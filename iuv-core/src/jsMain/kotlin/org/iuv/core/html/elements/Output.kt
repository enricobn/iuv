package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Output<MESSAGE> : HTML<MESSAGE>("output")
 ,PhrasingContentElement<MESSAGE>
 
 
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