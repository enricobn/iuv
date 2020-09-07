package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.ObjectAttributeGroup
import org.iuv.core.html.groups.FlowContent

open class FlowContentObject<MESSAGE> : HTML<MESSAGE>("object")
 
 ,GlobalAttributeGroup<MESSAGE>,ObjectAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {

    fun param(init: Param<MESSAGE>.() -> Unit) {
        element(Param(), init)
    }

}