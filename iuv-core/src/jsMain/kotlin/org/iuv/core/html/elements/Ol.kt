package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.OlType
import org.iuv.core.html.enums.Reversed

open class Ol<MESSAGE> : HTML<MESSAGE>("ol")
 
 ,GlobalAttributeGroup<MESSAGE>
 
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

    var reversed: Reversed?
        set(value) {
            if (value == null) {
                removeProperty("reversed")
            } else {
                addProperty("reversed", value.value)
            }
        }
        get() = Reversed.fromValue(getProperty("reversed"))


    fun li(init: OlLi<MESSAGE>.() -> Unit) {
        element(OlLi(), init)
    }

}