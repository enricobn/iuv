package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface ThAttributeGroup : HTMLElementAttributes
 
 {
    var colspan: Int?
        set(value) {
            if (value == null) {
                removeProperty("colspan")
            } else {
                addProperty("colspan", value)
            }
        }
        get() = (getProperty("colspan"))

    var rowspan: Int?
        set(value) {
            if (value == null) {
                removeProperty("rowspan")
            } else {
                addProperty("rowspan", value)
            }
        }
        get() = (getProperty("rowspan"))

}