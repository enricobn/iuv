package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Scoped

open class Style<MESSAGE> : HTML<MESSAGE>("style")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var scoped: Scoped?
        set(value) {
            if (value == null) {
                removeProperty("scoped")
            } else {
                addProperty("scoped", value.value)
            }
        }
        get() = Scoped.fromValue(getProperty("scoped"))



}