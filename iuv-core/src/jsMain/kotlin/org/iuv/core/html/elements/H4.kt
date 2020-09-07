package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class H4<MESSAGE> : HTML<MESSAGE>("h4")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {


}