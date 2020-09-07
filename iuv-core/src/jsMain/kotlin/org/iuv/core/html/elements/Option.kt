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
                removeProperty("disabled")
            } else {
                addProperty("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getProperty("disabled"))

    var selected: Selected?
        set(value) {
            if (value == null) {
                removeProperty("selected")
            } else {
                addProperty("selected", value.value)
            }
        }
        get() = Selected.fromValue(getProperty("selected"))

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