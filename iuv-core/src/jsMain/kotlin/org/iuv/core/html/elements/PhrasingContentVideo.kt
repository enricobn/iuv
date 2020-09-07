package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.VideoAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class PhrasingContentVideo<MESSAGE> : HTML<MESSAGE>("video")
 
 ,GlobalAttributeGroup<MESSAGE>,VideoAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {

    fun source(init: Source<MESSAGE>.() -> Unit) {
        element(Source(), init)
    }
    fun track(init: Track<MESSAGE>.() -> Unit) {
        element(Track(), init)
    }

}