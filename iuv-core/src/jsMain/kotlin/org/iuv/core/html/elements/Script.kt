package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Script<MESSAGE> : org.iuv.core.HTML<MESSAGE>("script")
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

    var defer: String?
        set(value) {
            if (value == null) {
                removeProperty("defer")
            } else {
                addProperty("defer", value)
            }
        }
        get() = (getProperty("defer"))

    var async: String?
        set(value) {
            if (value == null) {
                removeProperty("async")
            } else {
                addProperty("async", value)
            }
        }
        get() = (getProperty("async"))

    var charset: String?
        set(value) {
            if (value == null) {
                removeProperty("charset")
            } else {
                addProperty("charset", value)
            }
        }
        get() = (getProperty("charset"))



}