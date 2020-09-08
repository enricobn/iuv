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
                removeAttribute("type")
            } else {
                addAttribute("type", value.value)
            }
        }
        get() = OlType.fromValue(getAttribute("type"))
    var start: Int?
        set(value) {
            if (value == null) {
                removeAttribute("start")
            } else {
                addAttribute("start", value)
            }
        }
        get() = (getAttribute("start"))
    var reversed: Reversed?
        set(value) {
            if (value == null) {
                removeAttribute("reversed")
            } else {
                addAttribute("reversed", value.value)
            }
        }
        get() = Reversed.fromValue(getAttribute("reversed"))

    fun li(init: OlLi<MESSAGE>.() -> Unit) {
        element(OlLi(), init)
    }

}