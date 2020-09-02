package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Optgroup<MESSAGE> : org.iuv.core.HTML<MESSAGE>("optgroup")
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

    var disabled: String?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value)
            }
        }
        get() = (getProperty("disabled"))


    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }

}