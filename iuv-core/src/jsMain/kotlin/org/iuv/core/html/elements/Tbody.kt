package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Tbody<MESSAGE> : org.iuv.core.HTML<MESSAGE>("tbody")
 ,GlobalAttributeGroup
 
 {

    fun tr(init: Tr<MESSAGE>.() -> Unit) {
        element(Tr(), init)
    }
}