package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.MetaDataElements

open class Head<MESSAGE> : HTML<MESSAGE>("head")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,MetaDataElements<MESSAGE>
 {

    fun title(init: Title<MESSAGE>.() -> Unit) {
        element(Title(), init)
    }
    fun base(init: Base<MESSAGE>.() -> Unit) {
        element(Base(), init)
    }

}