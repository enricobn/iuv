package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Details<MESSAGE> : org.iuv.core.HTML<MESSAGE>("details")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {
    var open: String?
        set(value) {
            if (value == null) {
                removeProperty("open")
            } else {
                addProperty("open", value)
            }
        }
        get() = (getProperty("open"))


    fun summary(init: Summary<MESSAGE>.() -> Unit) {
        element(Summary(), init)
    }
}