package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Tr<MESSAGE> : HTML<MESSAGE>("tr")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {

    fun td(init: Td<MESSAGE>.() -> Unit) {
        element(Td(), init)
    }
    fun th(init: Th<MESSAGE>.() -> Unit) {
        element(Th(), init)
    }

}