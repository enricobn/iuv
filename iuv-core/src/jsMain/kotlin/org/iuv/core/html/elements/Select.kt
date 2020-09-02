package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Select<MESSAGE> : org.iuv.core.HTML<MESSAGE>("select")
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

    var size: Int?
        set(value) {
            if (value == null) {
                removeProperty("size")
            } else {
                addProperty("size", value)
            }
        }
        get() = (getProperty("size"))

    var multiple: String?
        set(value) {
            if (value == null) {
                removeProperty("multiple")
            } else {
                addProperty("multiple", value)
            }
        }
        get() = (getProperty("multiple"))

    var autofocus: String?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value)
            }
        }
        get() = (getProperty("autofocus"))

    var required: String?
        set(value) {
            if (value == null) {
                removeProperty("required")
            } else {
                addProperty("required", value)
            }
        }
        get() = (getProperty("required"))


    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }
    fun optgroup(init: Optgroup<MESSAGE>.() -> Unit) {
        element(Optgroup(), init)
    }

}