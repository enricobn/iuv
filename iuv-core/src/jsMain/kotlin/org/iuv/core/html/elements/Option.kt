package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Disabled
import org.iuv.core.html.enums.Selected

open class Option<MESSAGE> : HTML<MESSAGE>("option")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeAttribute("disabled")
            } else {
                addAttribute("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getAttribute("disabled"))
    var selected: Selected?
        set(value) {
            if (value == null) {
                removeAttribute("selected")
            } else {
                addAttribute("selected", value.value)
            }
        }
        get() = Selected.fromValue(getAttribute("selected"))
    var label: String?
        set(value) {
            if (value == null) {
                removeAttribute("label")
            } else {
                addAttribute("label", value)
            }
        }
        get() = (getAttribute("label"))
    var value: String?
        set(value) {
            if (value == null) {
                removeAttribute("value")
            } else {
                addAttribute("value", value)
            }
        }
        get() = (getAttribute("value"))


}