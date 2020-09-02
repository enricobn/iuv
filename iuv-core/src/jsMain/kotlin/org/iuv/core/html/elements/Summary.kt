package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Summary<MESSAGE> : org.iuv.core.HTML<MESSAGE>("summary")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

}