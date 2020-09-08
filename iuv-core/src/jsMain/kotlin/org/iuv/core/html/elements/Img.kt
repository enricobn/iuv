package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Ismap

open class Img<MESSAGE> : HTML<MESSAGE>("img")
 
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
    var alt: String?
        set(value) {
            if (value == null) {
                removeAttribute("alt")
            } else {
                addAttribute("alt", value)
            }
        }
        get() = (getAttribute("alt"))
    var height: Int?
        set(value) {
            if (value == null) {
                removeAttribute("height")
            } else {
                addAttribute("height", value)
            }
        }
        get() = (getAttribute("height"))
    var width: Int?
        set(value) {
            if (value == null) {
                removeAttribute("width")
            } else {
                addAttribute("width", value)
            }
        }
        get() = (getAttribute("width"))
    var usemap: String?
        set(value) {
            if (value == null) {
                removeAttribute("usemap")
            } else {
                addAttribute("usemap", value)
            }
        }
        get() = (getAttribute("usemap"))
    var ismap: Ismap?
        set(value) {
            if (value == null) {
                removeAttribute("ismap")
            } else {
                addAttribute("ismap", value.value)
            }
        }
        get() = Ismap.fromValue(getAttribute("ismap"))


}