package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.AudioAttributeGroup
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class PhrasingContentAudio<MESSAGE> : HTML<MESSAGE>("audio")
 
 ,GlobalAttributeGroup<MESSAGE>,AudioAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }

}