package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Link<MESSAGE> : HTML<MESSAGE>("link")
 
 ,GlobalAttributeGroup<MESSAGE>
 
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
    var sizes: String?
        set(value) {
            if (value == null) {
                removeAttribute("sizes")
            } else {
                addAttribute("sizes", value)
            }
        }
        get() = (getAttribute("sizes"))


}