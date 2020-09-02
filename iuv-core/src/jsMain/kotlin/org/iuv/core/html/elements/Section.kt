package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Section<MESSAGE> : org.iuv.core.HTML<MESSAGE>("section")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun style(init: Style<MESSAGE>.() -> Unit) {
        element(Style(), init)
    }
}