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
                removeProperty("label")
            } else {
                addProperty("label", value)
            }
        }
        get() = (getProperty("label"))

    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getProperty("disabled"))


    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }

}