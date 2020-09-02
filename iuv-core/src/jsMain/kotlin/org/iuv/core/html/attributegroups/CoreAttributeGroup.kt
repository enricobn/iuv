package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.enums.Dir

interface CoreAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 ,CoreAttributeGroupNodir<MESSAGE>
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