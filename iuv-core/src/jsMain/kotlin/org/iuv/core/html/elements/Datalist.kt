package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Datalist<MESSAGE> : org.iuv.core.HTML<MESSAGE>("datalist")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }
}