package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.VideoAttributeGroup

class FlowContentVideo<MESSAGE> : org.iuv.core.HTML<MESSAGE>("video")
 ,GlobalAttributeGroup,VideoAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }
}