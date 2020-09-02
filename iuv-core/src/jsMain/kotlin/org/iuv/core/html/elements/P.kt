package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class P<MESSAGE> : org.iuv.core.HTML<MESSAGE>("p")
 ,GlobalAttributeGroup<MESSAGE>
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {


}