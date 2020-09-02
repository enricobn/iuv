package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Option<MESSAGE> : org.iuv.core.HTML<MESSAGE>("option")
 ,GlobalAttributeGroup
 
 {
    var disabled: String?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value)
            }
        }
        get() = (getProperty("disabled"))

    var selected: String?
        set(value) {
            if (value == null) {
                removeProperty("selected")
            } else {
                addProperty("selected", value)
            }
        }
        get() = (getProperty("selected"))

    var label: String?
        set(value) {
            if (value == null) {
                removeProperty("label")
            } else {
                addProperty("label", value)
            }
        }
        get() = (getProperty("label"))

    var value: String?
        set(value) {
            if (value == null) {
                removeProperty("value")
            } else {
                addProperty("value", value)
            }
        }
        get() = (getProperty("value"))


}