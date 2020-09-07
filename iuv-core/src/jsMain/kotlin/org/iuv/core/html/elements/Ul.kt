package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Ul<MESSAGE> : HTML<MESSAGE>("ul")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {

    fun li(init: UlLi<MESSAGE>.() -> Unit) {
        element(UlLi(), init)
    }

}