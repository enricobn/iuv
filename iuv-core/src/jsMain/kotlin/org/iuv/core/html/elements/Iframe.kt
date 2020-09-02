package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Iframe<MESSAGE> : org.iuv.core.HTML<MESSAGE>("iframe")
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

    var srcdoc: String?
        set(value) {
            if (value == null) {
                removeProperty("srcdoc")
            } else {
                addProperty("srcdoc", value)
            }
        }
        get() = (getProperty("srcdoc"))

    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))

    var width: Int?
        set(value) {
            if (value == null) {
                removeProperty("width")
            } else {
                addProperty("width", value)
            }
        }
        get() = (getProperty("width"))

    var height: Int?
        set(value) {
            if (value == null) {
                removeProperty("height")
            } else {
                addProperty("height", value)
            }
        }
        get() = (getProperty("height"))

    var seamless: String?
        set(value) {
            if (value == null) {
                removeProperty("seamless")
            } else {
                addProperty("seamless", value)
            }
        }
        get() = (getProperty("seamless"))



}