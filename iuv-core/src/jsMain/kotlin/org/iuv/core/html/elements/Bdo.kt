package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.CommonEventsGroup
import org.iuv.core.html.attributegroups.CoreAttributeGroupNodir
import org.iuv.core.html.attributegroups.XmlAttributeGroup
import org.iuv.core.html.enums.Dir

class Bdo<MESSAGE> : org.iuv.core.HTML<MESSAGE>("bdo")
 ,CoreAttributeGroupNodir<MESSAGE>,CommonEventsGroup<MESSAGE>,XmlAttributeGroup<MESSAGE>
 ,org.iuv.core.html.groups.PhrasingContent<MESSAGE>
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