package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Meter<MESSAGE> : HTML<MESSAGE>("meter")
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
    var min: Float?
        set(value) {
            if (value == null) {
                removeAttribute("min")
            } else {
                addAttribute("min", value)
            }
        }
        get() = (getAttribute("min"))
    var low: Float?
        set(value) {
            if (value == null) {
                removeAttribute("low")
            } else {
                addAttribute("low", value)
            }
        }
        get() = (getAttribute("low"))
    var high: Float?
        set(value) {
            if (value == null) {
                removeAttribute("high")
            } else {
                addAttribute("high", value)
            }
        }
        get() = (getAttribute("high"))
    var max: Float?
        set(value) {
            if (value == null) {
                removeAttribute("max")
            } else {
                addAttribute("max", value)
            }
        }
        get() = (getAttribute("max"))
    var optimum: Float?
        set(value) {
            if (value == null) {
                removeAttribute("optimum")
            } else {
                addAttribute("optimum", value)
            }
        }
        get() = (getAttribute("optimum"))
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