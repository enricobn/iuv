package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Caption<MESSAGE> : org.iuv.core.HTML<MESSAGE>("caption")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {

}