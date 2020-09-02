package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Wrap

class Textarea<MESSAGE> : org.iuv.core.HTML<MESSAGE>("textarea")
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

    var readonly: String?
        set(value) {
            if (value == null) {
                removeProperty("readonly")
            } else {
                addProperty("readonly", value)
            }
        }
        get() = (getProperty("readonly"))

    var maxlength: Int?
        set(value) {
            if (value == null) {
                removeProperty("maxlength")
            } else {
                addProperty("maxlength", value)
            }
        }
        get() = (getProperty("maxlength"))

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