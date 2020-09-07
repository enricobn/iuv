package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class Rt<MESSAGE> : HTML<MESSAGE>("rt")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {


}