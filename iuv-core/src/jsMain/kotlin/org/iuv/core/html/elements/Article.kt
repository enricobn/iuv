package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Article<MESSAGE> : org.iuv.core.HTML<MESSAGE>("article")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun style(init: Style<MESSAGE>.() -> Unit) {
        element(Style(), init)
    }
}