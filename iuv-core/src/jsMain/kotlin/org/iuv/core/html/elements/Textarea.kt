package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.*

open class Textarea<MESSAGE> : HTML<MESSAGE>("textarea")
 
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

    var readonly: Readonly?
        set(value) {
            if (value == null) {
                removeProperty("readonly")
            } else {
                addProperty("readonly", value.value)
            }
        }
        get() = Readonly.fromValue(getProperty("readonly"))

    var maxlength: Int?
        set(value) {
            if (value == null) {
                removeProperty("maxlength")
            } else {
                addProperty("maxlength", value)
            }
        }
        get() = (getProperty("maxlength"))

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

    var placeholder: String?
        set(value) {
            if (value == null) {
                removeProperty("placeholder")
            } else {
                addProperty("placeholder", value)
            }
        }
        get() = (getProperty("placeholder"))

    var rows: Int?
        set(value) {
            if (value == null) {
                removeProperty("rows")
            } else {
                addProperty("rows", value)
            }
        }
        get() = (getProperty("rows"))

    var cols: Int?
        set(value) {
            if (value == null) {
                removeProperty("cols")
            } else {
                addProperty("cols", value)
            }
        }
        get() = (getProperty("cols"))

    var wrap: Wrap?
        set(value) {
            if (value == null) {
                removeProperty("wrap")
            } else {
                addProperty("wrap", value.value)
            }
        }
        get() = Wrap.fromValue(getProperty("wrap"))



}