package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.AudioAttributeGroup
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class FlowContentAudio<MESSAGE> : org.iuv.core.HTML<MESSAGE>("audio")
 ,GlobalAttributeGroup<MESSAGE>,AudioAttributeGroup<MESSAGE>
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }

}