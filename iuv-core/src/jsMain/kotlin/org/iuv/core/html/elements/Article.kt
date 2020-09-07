package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.FlowContent

open class Article<MESSAGE> : HTML<MESSAGE>("article")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {

    fun style(init: Style<MESSAGE>.() -> Unit) {
        element(Style(), init)
    }

}