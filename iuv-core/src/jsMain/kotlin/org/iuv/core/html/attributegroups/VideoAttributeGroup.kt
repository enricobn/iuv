package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.enums.Muted

interface VideoAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 ,AudioAttributeGroup<MESSAGE>,ObjectAttributeGroupVideoAttributeGroup<MESSAGE>
 {
    var poster: String?
        set(value) {
            if (value == null) {
                removeProperty("poster")
            } else {
                addProperty("poster", value)
            }
        }
        get() = (getProperty("poster"))

    var muted: Muted?
        set(value) {
            if (value == null) {
                removeProperty("muted")
            } else {
                addProperty("muted", value.value)
            }
        }
        get() = Muted.fromValue(getProperty("muted"))


}