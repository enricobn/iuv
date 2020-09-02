package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface AAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var href: String?
        set(value) {
            if (value == null) {
                removeProperty("href")
            } else {
                addProperty("href", value)
            }
        }
        get() = (getProperty("href"))

    var rel: String?
        set(value) {
            if (value == null) {
                removeProperty("rel")
            } else {
                addProperty("rel", value)
            }
        }
        get() = (getProperty("rel"))


}