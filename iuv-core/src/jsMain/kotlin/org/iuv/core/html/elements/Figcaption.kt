package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.FlowContent

open class Figcaption<MESSAGE> : HTML<MESSAGE>("figcaption")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {


}