package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.AudioAttributeGroup
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class PhrasingContentAudio<MESSAGE> : org.iuv.core.HTML<MESSAGE>("audio")
 ,GlobalAttributeGroup,AudioAttributeGroup
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }
}