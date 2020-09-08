package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Progress<MESSAGE> : HTML<MESSAGE>("progress")
 ,PhrasingContentElement<MESSAGE>
 
 
 {
    var value: Float?
        set(value) {
            if (value == null) {
                removeAttribute("value")
            } else {
                addAttribute("value", value)
            }
        }
        get() = (getAttribute("value"))
    var max: Float?
        set(value) {
            if (value == null) {
                removeAttribute("max")
            } else {
                addAttribute("max", value)
            }
        }
        get() = (getAttribute("max"))
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