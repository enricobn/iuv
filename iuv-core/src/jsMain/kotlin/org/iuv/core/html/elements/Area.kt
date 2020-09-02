package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Shape

class Area<MESSAGE> : org.iuv.core.HTML<MESSAGE>("area")
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var alt: String?
        set(value) {
            if (value == null) {
                removeProperty("alt")
            } else {
                addProperty("alt", value)
            }
        }
        get() = (getProperty("alt"))

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

    var shape: Shape?
        set(value) {
            if (value == null) {
                removeProperty("shape")
            } else {
                addProperty("shape", value.value)
            }
        }
        get() = Shape.fromValue(getProperty("shape"))

    var coords: String?
        set(value) {
            if (value == null) {
                removeProperty("coords")
            } else {
                addProperty("coords", value)
            }
        }
        get() = (getProperty("coords"))



}