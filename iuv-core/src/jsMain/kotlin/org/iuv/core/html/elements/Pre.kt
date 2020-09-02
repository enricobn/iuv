package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Pre<MESSAGE> : org.iuv.core.HTML<MESSAGE>("pre")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

}