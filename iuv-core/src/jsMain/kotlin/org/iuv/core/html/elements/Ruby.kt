package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class Ruby<MESSAGE> : HTML<MESSAGE>("ruby")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {

    fun rt(init: Rt<MESSAGE>.() -> Unit) {
        element(Rt(), init)
    }
    fun rp(init: Rp<MESSAGE>.() -> Unit) {
        element(Rp(), init)
    }

}