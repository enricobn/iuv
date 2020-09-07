package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Tfoot<MESSAGE> : HTML<MESSAGE>("tfoot")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {

    fun tr(init: Tr<MESSAGE>.() -> Unit) {
        element(Tr(), init)
    }

}