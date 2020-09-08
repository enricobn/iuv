package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Disabled

open class Optgroup<MESSAGE> : HTML<MESSAGE>("optgroup")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var label: String?
        set(value) {
            if (value == null) {
                removeAttribute("label")
            } else {
                addAttribute("label", value)
            }
        }
        get() = (getAttribute("label"))
    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeAttribute("disabled")
            } else {
                addAttribute("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getAttribute("disabled"))

    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }

}