package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Aside<MESSAGE> : org.iuv.core.HTML<MESSAGE>("aside")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun style(init: Style<MESSAGE>.() -> Unit) {
        element(Style(), init)
    }
}