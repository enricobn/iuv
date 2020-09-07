package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.ObjectAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class PhrasingContentObject<MESSAGE> : HTML<MESSAGE>("object")
 
 ,GlobalAttributeGroup<MESSAGE>,ObjectAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {

    fun param(init: Param<MESSAGE>.() -> Unit) {
        element(Param(), init)
    }

}