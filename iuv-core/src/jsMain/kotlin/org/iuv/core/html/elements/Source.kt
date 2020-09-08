package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Source<MESSAGE> : HTML<MESSAGE>("source")
 
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


}