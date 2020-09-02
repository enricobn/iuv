package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Col<MESSAGE> : org.iuv.core.HTML<MESSAGE>("col")
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var span: Int?
        set(value) {
            if (value == null) {
                removeProperty("span")
            } else {
                addProperty("span", value)
            }
        }
        get() = (getProperty("span"))



}