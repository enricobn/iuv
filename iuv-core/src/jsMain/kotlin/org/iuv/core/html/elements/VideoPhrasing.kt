package org.iuv.core.html.elements
import org.iuv.core.HTMLChild
import org.iuv.core.HTMLElement
import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.attributegroups.VideoAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

interface VideoPhrasing<MESSAGE> : HTMLChild, HTMLElement<MESSAGE>, HTMLElementAttributes<MESSAGE>
 
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