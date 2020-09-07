package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Dl<MESSAGE> : HTML<MESSAGE>("dl")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {

    fun dt(init: Dt<MESSAGE>.() -> Unit) {
        element(Dt(), init)
    }
    fun dd(init: Dd<MESSAGE>.() -> Unit) {
        element(Dd(), init)
    }

}