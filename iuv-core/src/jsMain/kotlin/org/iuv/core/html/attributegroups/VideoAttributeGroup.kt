package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.enums.Muted

interface VideoAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 ,AudioAttributeGroup<MESSAGE>,ObjectAttributeGroupVideoAttributeGroup<MESSAGE>
 {
    var poster: String?
        set(value) {
            if (value == null) {
                removeAttribute("poster")
            } else {
                addAttribute("poster", value)
            }
        }
        get() = (getAttribute("poster"))
    var muted: Muted?
        set(value) {
            if (value == null) {
                removeAttribute("muted")
            } else {
                addAttribute("muted", value.value)
            }
        }
        get() = Muted.fromValue(getAttribute("muted"))

}