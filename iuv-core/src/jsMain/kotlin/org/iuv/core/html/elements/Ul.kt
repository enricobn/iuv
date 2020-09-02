package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Ul<MESSAGE> : org.iuv.core.HTML<MESSAGE>("ul")
 ,GlobalAttributeGroup<MESSAGE>
 
 {

    fun li(init: UlLi<MESSAGE>.() -> Unit) {
        element(UlLi(), init)
    }

}