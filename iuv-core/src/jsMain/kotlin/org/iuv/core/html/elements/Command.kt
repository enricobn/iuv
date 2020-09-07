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
                removeProperty("type")
            } else {
                addProperty("type", value.value)
            }
        }
        get() = CommandType.fromValue(getProperty("type"))

    var label: String?
        set(value) {
            if (value == null) {
                removeProperty("label")
            } else {
                addProperty("label", value)
            }
        }
        get() = (getProperty("label"))

    var icon: String?
        set(value) {
            if (value == null) {
                removeProperty("icon")
            } else {
                addProperty("icon", value)
            }
        }
        get() = (getProperty("icon"))

    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getProperty("disabled"))

    var radiogroup: String?
        set(value) {
            if (value == null) {
                removeProperty("radiogroup")
            } else {
                addProperty("radiogroup", value)
            }
        }
        get() = (getProperty("radiogroup"))

    var checked: Checked?
        set(value) {
            if (value == null) {
                removeProperty("checked")
            } else {
                addProperty("checked", value.value)
            }
        }
        get() = Checked.fromValue(getProperty("checked"))



}