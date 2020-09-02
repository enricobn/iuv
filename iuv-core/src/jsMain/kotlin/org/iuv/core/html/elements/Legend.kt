package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Legend<MESSAGE> : org.iuv.core.HTML<MESSAGE>("legend")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
 {

}