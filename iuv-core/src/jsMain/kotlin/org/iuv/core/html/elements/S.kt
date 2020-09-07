package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class S<MESSAGE> : HTML<MESSAGE>("s")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {


}