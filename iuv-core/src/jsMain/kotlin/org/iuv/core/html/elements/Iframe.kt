package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Seamless

open class Iframe<MESSAGE> : HTML<MESSAGE>("iframe")
 
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

    var seamless: Seamless?
        set(value) {
            if (value == null) {
                removeProperty("seamless")
            } else {
                addProperty("seamless", value.value)
            }
        }
        get() = Seamless.fromValue(getProperty("seamless"))



}