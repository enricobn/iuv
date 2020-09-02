package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Link<MESSAGE> : org.iuv.core.HTML<MESSAGE>("link")
 ,GlobalAttributeGroup
 
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

    var sizes: String?
        set(value) {
            if (value == null) {
                removeProperty("sizes")
            } else {
                addProperty("sizes", value)
            }
        }
        get() = (getProperty("sizes"))


}