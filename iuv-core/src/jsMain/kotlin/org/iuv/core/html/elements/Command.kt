package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.CommandType

class Command<MESSAGE> : org.iuv.core.HTML<MESSAGE>("command")
 ,GlobalAttributeGroup
 
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

    var disabled: String?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value)
            }
        }
        get() = (getProperty("disabled"))

    var radiogroup: String?
        set(value) {
            if (value == null) {
                removeProperty("radiogroup")
            } else {
                addProperty("radiogroup", value)
            }
        }
        get() = (getProperty("radiogroup"))

    var checked: String?
        set(value) {
            if (value == null) {
                removeProperty("checked")
            } else {
                addProperty("checked", value)
            }
        }
        get() = (getProperty("checked"))


}