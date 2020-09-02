package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.OlType

class Ol<MESSAGE> : org.iuv.core.HTML<MESSAGE>("ol")
 ,GlobalAttributeGroup
 
 {
    var type: OlType?
        set(value) {
            if (value == null) {
                removeProperty("type")
            } else {
                addProperty("type", value.value)
            }
        }
        get() = OlType.fromValue(getProperty("type"))

    var start: Int?
        set(value) {
            if (value == null) {
                removeProperty("start")
            } else {
                addProperty("start", value)
            }
        }
        get() = (getProperty("start"))

    var reversed: String?
        set(value) {
            if (value == null) {
                removeProperty("reversed")
            } else {
                addProperty("reversed", value)
            }
        }
        get() = (getProperty("reversed"))


    fun li(init: OlLi<MESSAGE>.() -> Unit) {
        element(OlLi(), init)
    }
}