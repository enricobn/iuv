package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Thead<MESSAGE> : org.iuv.core.HTML<MESSAGE>("thead")
 ,GlobalAttributeGroup
 
 {

    fun tr(init: Tr<MESSAGE>.() -> Unit) {
        element(Tr(), init)
    }
}