package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.HttpEquiv

class Meta<MESSAGE> : org.iuv.core.HTML<MESSAGE>("meta")
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var httpequiv: HttpEquiv?
        set(value) {
            if (value == null) {
                removeProperty("http-equiv")
            } else {
                addProperty("http-equiv", value.value)
            }
        }
        get() = HttpEquiv.fromValue(getProperty("http-equiv"))

    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))

    var content: String?
        set(value) {
            if (value == null) {
                removeProperty("content")
            } else {
                addProperty("content", value)
            }
        }
        get() = (getProperty("content"))

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