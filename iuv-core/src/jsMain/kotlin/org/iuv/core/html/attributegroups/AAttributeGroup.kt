package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface AAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
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

}