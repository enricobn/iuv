package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.ObjectAttributeGroup

class FlowContentObject<MESSAGE> : org.iuv.core.HTML<MESSAGE>("object")
 ,GlobalAttributeGroup,ObjectAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun param(init: Param<MESSAGE>.() -> Unit) {
        element(Param(), init)
    }
}