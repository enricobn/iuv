package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class Mark<MESSAGE> : HTML<MESSAGE>("mark")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {


}