package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class Datalist<MESSAGE> : HTML<MESSAGE>("datalist")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {

    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }

}