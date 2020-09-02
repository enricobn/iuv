package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Div<MESSAGE> : org.iuv.core.HTML<MESSAGE>("div")
 ,GlobalAttributeGroup<MESSAGE>
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun style(init: Style<MESSAGE>.() -> Unit) {
        element(Style(), init)
    }

}