package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface ObjectAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 ,ObjectAttributeGroupVideoAttributeGroup<MESSAGE>
 {
    var data: String?
        set(value) {
            if (value == null) {
                removeAttribute("data")
            } else {
                addAttribute("data", value)
            }
        }
        get() = (getAttribute("data"))
    var usemap: String?
        set(value) {
            if (value == null) {
                removeAttribute("usemap")
            } else {
                addAttribute("usemap", value)
            }
        }
        get() = (getAttribute("usemap"))
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