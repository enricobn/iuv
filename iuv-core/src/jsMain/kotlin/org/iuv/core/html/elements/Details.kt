package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Open
import org.iuv.core.html.groups.FlowContent

open class Details<MESSAGE> : HTML<MESSAGE>("details")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {
    var open_: Open?
        set(value) {
            if (value == null) {
                removeProperty("open")
            } else {
                addProperty("open", value.value)
            }
        }
        get() = Open.fromValue(getProperty("open"))


    fun summary(init: Summary<MESSAGE>.() -> Unit) {
        element(Summary(), init)
    }

}