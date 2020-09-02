package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface ObjectAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var data: String?
        set(value) {
            if (value == null) {
                removeProperty("data")
            } else {
                addProperty("data", value)
            }
        }
        get() = (getProperty("data"))

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

    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))

    var form: String?
        set(value) {
            if (value == null) {
                removeProperty("form")
            } else {
                addProperty("form", value)
            }
        }
        get() = (getProperty("form"))


}