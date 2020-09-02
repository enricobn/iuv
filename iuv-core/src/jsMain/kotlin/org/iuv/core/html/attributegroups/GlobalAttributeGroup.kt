package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface GlobalAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 ,CoreAttributeGroup<MESSAGE>,CommonEventsGroup<MESSAGE>,XmlAttributeGroup<MESSAGE>
 {

}