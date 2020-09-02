package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Source<MESSAGE> : org.iuv.core.HTML<MESSAGE>("source")
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var src: String?
        set(value) {
            if (value == null) {
                removeProperty("src")
            } else {
                addProperty("src", value)
            }
        }
        get() = (getProperty("src"))



}