package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Ismap

open class Img<MESSAGE> : HTML<MESSAGE>("img")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var src: String?
        set(value) {
            if (value == null) {
                removeProperty("src")
            } else {
                addProperty("src", value)
            }
        }
        get() = (getProperty("src"))

    var alt: String?
        set(value) {
            if (value == null) {
                removeProperty("alt")
            } else {
                addProperty("alt", value)
            }
        }
        get() = (getProperty("alt"))

    var height: Int?
        set(value) {
            if (value == null) {
                removeProperty("height")
            } else {
                addProperty("height", value)
            }
        }
        get() = (getProperty("height"))

    var width: Int?
        set(value) {
            if (value == null) {
                removeProperty("width")
            } else {
                addProperty("width", value)
            }
        }
        get() = (getProperty("width"))

    var usemap: String?
        set(value) {
            if (value == null) {
                removeProperty("usemap")
            } else {
                addProperty("usemap", value)
            }
        }
        get() = (getProperty("usemap"))

    var ismap: Ismap?
        set(value) {
            if (value == null) {
                removeProperty("ismap")
            } else {
                addProperty("ismap", value.value)
            }
        }
        get() = Ismap.fromValue(getProperty("ismap"))



}