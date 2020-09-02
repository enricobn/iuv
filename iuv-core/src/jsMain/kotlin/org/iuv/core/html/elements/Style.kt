package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Style<MESSAGE> : org.iuv.core.HTML<MESSAGE>("style")
 ,GlobalAttributeGroup
 
 {
    var scoped: String?
        set(value) {
            if (value == null) {
                removeProperty("scoped")
            } else {
                addProperty("scoped", value)
            }
        }
        get() = (getProperty("scoped"))


}