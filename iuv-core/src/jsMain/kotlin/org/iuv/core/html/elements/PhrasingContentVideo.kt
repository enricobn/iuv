package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.VideoAttributeGroup

class PhrasingContentVideo<MESSAGE> : org.iuv.core.HTML<MESSAGE>("video")
 ,GlobalAttributeGroup<MESSAGE>,VideoAttributeGroup<MESSAGE>
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }

}