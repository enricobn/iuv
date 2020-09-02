package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Tr<MESSAGE> : org.iuv.core.HTML<MESSAGE>("tr")
 ,GlobalAttributeGroup
 
 {

    fun td(init: Td<MESSAGE>.() -> Unit) {
        element(Td(), init)
    }
    fun th(init: Th<MESSAGE>.() -> Unit) {
        element(Th(), init)
    }
}