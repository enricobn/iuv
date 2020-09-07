package org.iuv.core.html.elements
import org.iuv.core.HTMLChild
import org.iuv.core.HTMLElement
import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.attributegroups.AAttributeGroup

interface AFlow<MESSAGE> : HTMLChild, HTMLElement<MESSAGE>, HTMLElementAttributes<MESSAGE>
 
 ,AAttributeGroup<MESSAGE>
 
 {


}