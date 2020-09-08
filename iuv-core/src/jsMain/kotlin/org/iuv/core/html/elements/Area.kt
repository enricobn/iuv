package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Shape

open class Area<MESSAGE> : HTML<MESSAGE>("area")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var alt: String?
        set(value) {
            if (value == null) {
                removeAttribute("alt")
            } else {
                addAttribute("alt", value)
            }
        }
        get() = (getAttribute("alt"))
    var href: String?
        set(value) {
            if (value == null) {
                removeAttribute("href")
            } else {
                addAttribute("href", value)
            }
        }
        get() = (getAttribute("href"))
    var rel: String?
        set(value) {
            if (value == null) {
                removeAttribute("rel")
            } else {
                addAttribute("rel", value)
            }
        }
        get() = (getAttribute("rel"))
    var shape: Shape?
        set(value) {
            if (value == null) {
                removeAttribute("shape")
            } else {
                addAttribute("shape", value.value)
            }
        }
        get() = Shape.fromValue(getAttribute("shape"))
    var coords: String?
        set(value) {
            if (value == null) {
                removeAttribute("coords")
            } else {
                addAttribute("coords", value)
            }
        }
        get() = (getAttribute("coords"))


}