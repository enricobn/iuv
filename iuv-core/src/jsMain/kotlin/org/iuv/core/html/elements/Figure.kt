package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.FlowContent

open class Figure<MESSAGE> : HTML<MESSAGE>("figure")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {

    fun figcaption(init: Figcaption<MESSAGE>.() -> Unit) {
        element(Figcaption(), init)
    }

}