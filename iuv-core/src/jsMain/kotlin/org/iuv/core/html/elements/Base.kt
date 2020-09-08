package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Base<MESSAGE> : HTML<MESSAGE>("base")
 
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


}