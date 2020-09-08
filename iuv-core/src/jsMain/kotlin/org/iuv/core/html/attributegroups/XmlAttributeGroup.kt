package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface XmlAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var xml_lang: String?
        set(value) {
            if (value == null) {
                removeAttribute("xml:lang")
            } else {
                addAttribute("xml:lang", value)
            }
        }
        get() = (getAttribute("xml:lang"))
    var xml_space: String?
        set(value) {
            if (value == null) {
                removeAttribute("xml:space")
            } else {
                addAttribute("xml:space", value)
            }
        }
        get() = (getAttribute("xml:space"))
    var xml_base: String?
        set(value) {
            if (value == null) {
                removeAttribute("xml:base")
            } else {
                addAttribute("xml:base", value)
            }
        }
        get() = (getAttribute("xml:base"))

}