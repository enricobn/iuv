package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Fieldset<MESSAGE> : org.iuv.core.HTML<MESSAGE>("fieldset")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
 {
    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))

    var disabled: String?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value)
            }
        }
        get() = (getProperty("disabled"))

    var form: String?
        set(value) {
            if (value == null) {
                removeProperty("form")
            } else {
                addProperty("form", value)
            }
        }
        get() = (getProperty("form"))


    fun legend(init: Legend<MESSAGE>.() -> Unit) {
        element(Legend(), init)
    }
}