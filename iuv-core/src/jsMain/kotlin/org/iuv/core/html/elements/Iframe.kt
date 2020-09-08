package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Seamless

open class Iframe<MESSAGE> : HTML<MESSAGE>("iframe")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var src: String?
        set(value) {
            if (value == null) {
                removeAttribute("src")
            } else {
                addAttribute("src", value)
            }
        }
        get() = (getAttribute("src"))
    var srcdoc: String?
        set(value) {
            if (value == null) {
                removeAttribute("srcdoc")
            } else {
                addAttribute("srcdoc", value)
            }
        }
        get() = (getAttribute("srcdoc"))
    var name: String?
        set(value) {
            if (value == null) {
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))
    var width: Int?
        set(value) {
            if (value == null) {
                removeAttribute("width")
            } else {
                addAttribute("width", value)
            }
        }
        get() = (getAttribute("width"))
    var height: Int?
        set(value) {
            if (value == null) {
                removeAttribute("height")
            } else {
                addAttribute("height", value)
            }
        }
        get() = (getAttribute("height"))
    var seamless: Seamless?
        set(value) {
            if (value == null) {
                removeAttribute("seamless")
            } else {
                addAttribute("seamless", value.value)
            }
        }
        get() = Seamless.fromValue(getAttribute("seamless"))


}