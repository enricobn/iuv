package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface XmlAttributeGroup : HTMLElementAttributes
 
 {
    var xml_lang: String?
        set(value) {
            if (value == null) {
                removeProperty("xml:lang")
            } else {
                addProperty("xml:lang", value)
            }
        }
        get() = (getProperty("xml:lang"))

    var xml_space: String?
        set(value) {
            if (value == null) {
                removeProperty("xml:space")
            } else {
                addProperty("xml:space", value)
            }
        }
        get() = (getProperty("xml:space"))

    var xml_base: String?
        set(value) {
            if (value == null) {
                removeProperty("xml:base")
            } else {
                addProperty("xml:base", value)
            }
        }
        get() = (getProperty("xml:base"))

}