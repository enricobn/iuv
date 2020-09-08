package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Checked
import org.iuv.core.html.enums.CommandType
import org.iuv.core.html.enums.Disabled

open class Command<MESSAGE> : HTML<MESSAGE>("command")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var type: CommandType?
        set(value) {
            if (value == null) {
                removeAttribute("type")
            } else {
                addAttribute("type", value.value)
            }
        }
        get() = CommandType.fromValue(getAttribute("type"))
    var label: String?
        set(value) {
            if (value == null) {
                removeAttribute("label")
            } else {
                addAttribute("label", value)
            }
        }
        get() = (getAttribute("label"))
    var icon: String?
        set(value) {
            if (value == null) {
                removeAttribute("icon")
            } else {
                addAttribute("icon", value)
            }
        }
        get() = (getAttribute("icon"))
    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeAttribute("disabled")
            } else {
                addAttribute("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getAttribute("disabled"))
    var radiogroup: String?
        set(value) {
            if (value == null) {
                removeAttribute("radiogroup")
            } else {
                addAttribute("radiogroup", value)
            }
        }
        get() = (getAttribute("radiogroup"))
    var checked: Checked?
        set(value) {
            if (value == null) {
                removeAttribute("checked")
            } else {
                addAttribute("checked", value.value)
            }
        }
        get() = Checked.fromValue(getAttribute("checked"))


}