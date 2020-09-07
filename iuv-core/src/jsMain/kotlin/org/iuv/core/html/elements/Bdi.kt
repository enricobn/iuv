package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.CommonEventsGroup
import org.iuv.core.html.attributegroups.CoreAttributeGroupNodir
import org.iuv.core.html.attributegroups.XmlAttributeGroup
import org.iuv.core.html.enums.Dir
import org.iuv.core.html.groups.PhrasingContent

open class Bdi<MESSAGE> : HTML<MESSAGE>("bdi")
 
 ,CoreAttributeGroupNodir<MESSAGE>,CommonEventsGroup<MESSAGE>,XmlAttributeGroup<MESSAGE>
 ,PhrasingContent<MESSAGE>
 {
    var dir: Dir?
        set(value) {
            if (value == null) {
                removeProperty("dir")
            } else {
                addProperty("dir", value.value)
            }
        }
        get() = Dir.fromValue(getProperty("dir"))



}