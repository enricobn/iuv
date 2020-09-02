package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Tfoot<MESSAGE> : org.iuv.core.HTML<MESSAGE>("tfoot")
 ,GlobalAttributeGroup
 
 {

    fun tr(init: Tr<MESSAGE>.() -> Unit) {
        element(Tr(), init)
    }
}