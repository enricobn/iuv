package org.iuv.core.html.elements
import org.iuv.core.HTMLChild
import org.iuv.core.HTMLElement
import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

interface PhrasingContentElement<MESSAGE> : HTMLChild, HTMLElement<MESSAGE>, HTMLElementAttributes<MESSAGE>
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {


}