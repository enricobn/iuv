package org.iuv.core.html.elements
import org.iuv.core.HTMLChild
import org.iuv.core.HTMLElement
import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.ObjectAttributeGroup
import org.iuv.core.html.groups.FlowContent

interface ObjectFlow<MESSAGE> : HTMLChild, HTMLElement<MESSAGE>, HTMLElementAttributes<MESSAGE>
 
 ,GlobalAttributeGroup<MESSAGE>,ObjectAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {

    fun param(init: Param<MESSAGE>.() -> Unit) {
        element(Param(), init)
    }

}