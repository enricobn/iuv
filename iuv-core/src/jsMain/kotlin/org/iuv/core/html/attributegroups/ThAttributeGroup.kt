package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface ThAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var colspan: Int?
        set(value) {
            if (value == null) {
                removeAttribute("colspan")
            } else {
                addAttribute("colspan", value)
            }
        }
        get() = (getAttribute("colspan"))
    var rowspan: Int?
        set(value) {
            if (value == null) {
                removeAttribute("rowspan")
            } else {
                addAttribute("rowspan", value)
            }
        }
        get() = (getAttribute("rowspan"))

}