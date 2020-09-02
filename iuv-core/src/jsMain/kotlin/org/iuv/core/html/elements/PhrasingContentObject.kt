package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.ObjectAttributeGroup

class PhrasingContentObject<MESSAGE> : org.iuv.core.HTML<MESSAGE>("object")
 ,GlobalAttributeGroup<MESSAGE>,ObjectAttributeGroup<MESSAGE>
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

    fun param(init: Param<MESSAGE>.() -> Unit) {
        element(Param(), init)
    }

}