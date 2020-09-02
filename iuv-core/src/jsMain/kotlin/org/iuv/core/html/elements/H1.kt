package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class H1<MESSAGE> : org.iuv.core.HTML<MESSAGE>("h1")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

}