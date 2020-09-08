package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Col<MESSAGE> : HTML<MESSAGE>("col")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var span: Int?
        set(value) {
            if (value == null) {
                removeAttribute("span")
            } else {
                addAttribute("span", value)
            }
        }
        get() = (getAttribute("span"))


}