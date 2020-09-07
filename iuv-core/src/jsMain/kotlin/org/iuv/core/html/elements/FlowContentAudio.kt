package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.AudioAttributeGroup
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.FlowContent

open class FlowContentAudio<MESSAGE> : HTML<MESSAGE>("audio")
 
 ,GlobalAttributeGroup<MESSAGE>,AudioAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }

}