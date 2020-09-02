package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Base<MESSAGE> : org.iuv.core.HTML<MESSAGE>("base")
 ,GlobalAttributeGroup
 
 {
    var href: String?
        set(value) {
            if (value == null) {
                removeProperty("href")
            } else {
                addProperty("href", value)
            }
        }
        get() = (getProperty("href"))


}