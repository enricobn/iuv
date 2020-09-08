package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Param<MESSAGE> : HTML<MESSAGE>("param")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var name: String?
        set(value) {
            if (value == null) {
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))
    var value: String?
        set(value) {
            if (value == null) {
                removeAttribute("value")
            } else {
                addAttribute("value", value)
            }
        }
        get() = (getAttribute("value"))


}