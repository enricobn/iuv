package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.groups.PhrasingContent

open class H1<MESSAGE> : HTML<MESSAGE>("h1")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {


}