package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Autofocus
import org.iuv.core.html.enums.Disabled
import org.iuv.core.html.enums.Multiple
import org.iuv.core.html.enums.Required

open class Select<MESSAGE> : HTML<MESSAGE>("select")
 
 ,GlobalAttributeGroup<MESSAGE>
 
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

    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeProperty("disabled")
            } else {
                addProperty("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getProperty("disabled"))

    var form: String?
        set(value) {
            if (value == null) {
                removeProperty("form")
            } else {
                addProperty("form", value)
            }
        }
        get() = (getProperty("form"))

    var size: Int?
        set(value) {
            if (value == null) {
                removeProperty("size")
            } else {
                addProperty("size", value)
            }
        }
        get() = (getProperty("size"))

    var multiple: Multiple?
        set(value) {
            if (value == null) {
                removeProperty("multiple")
            } else {
                addProperty("multiple", value.value)
            }
        }
        get() = Multiple.fromValue(getProperty("multiple"))

    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getProperty("autofocus"))

    var required: Required?
        set(value) {
            if (value == null) {
                removeProperty("required")
            } else {
                addProperty("required", value.value)
            }
        }
        get() = Required.fromValue(getProperty("required"))


    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }
    fun optgroup(init: Optgroup<MESSAGE>.() -> Unit) {
        element(Optgroup(), init)
    }

}