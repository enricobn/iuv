package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.*

open class Input<MESSAGE> : HTML<MESSAGE>("input")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var accept: String?
        set(value) {
            if (value == null) {
                removeProperty("accept")
            } else {
                addProperty("accept", value)
            }
        }
        get() = (getProperty("accept"))

    var alt: String?
        set(value) {
            if (value == null) {
                removeProperty("alt")
            } else {
                addProperty("alt", value)
            }
        }
        get() = (getProperty("alt"))

    var autocomplete: Autocomplete?
        set(value) {
            if (value == null) {
                removeProperty("autocomplete")
            } else {
                addProperty("autocomplete", value.value)
            }
        }
        get() = Autocomplete.fromValue(getProperty("autocomplete"))

    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getProperty("autofocus"))

    var checked: Checked?
        set(value) {
            if (value == null) {
                removeProperty("checked")
            } else {
                addProperty("checked", value.value)
            }
        }
        get() = Checked.fromValue(getProperty("checked"))

    var dirname: String?
        set(value) {
            if (value == null) {
                removeProperty("dirname")
            } else {
                addProperty("dirname", value)
            }
        }
        get() = (getProperty("dirname"))

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

    var formaction: String?
        set(value) {
            if (value == null) {
                removeProperty("formaction")
            } else {
                addProperty("formaction", value)
            }
        }
        get() = (getProperty("formaction"))

    var formenctype: Formenctype?
        set(value) {
            if (value == null) {
                removeProperty("formenctype")
            } else {
                addProperty("formenctype", value.value)
            }
        }
        get() = Formenctype.fromValue(getProperty("formenctype"))

    var formmethod: Formmethod?
        set(value) {
            if (value == null) {
                removeProperty("formmethod")
            } else {
                addProperty("formmethod", value.value)
            }
        }
        get() = Formmethod.fromValue(getProperty("formmethod"))

    var formnovalidate: Formnovalidate?
        set(value) {
            if (value == null) {
                removeProperty("formnovalidate")
            } else {
                addProperty("formnovalidate", value.value)
            }
        }
        get() = Formnovalidate.fromValue(getProperty("formnovalidate"))

    var height: Int?
        set(value) {
            if (value == null) {
                removeProperty("height")
            } else {
                addProperty("height", value)
            }
        }
        get() = (getProperty("height"))

    var list: String?
        set(value) {
            if (value == null) {
                removeProperty("list")
            } else {
                addProperty("list", value)
            }
        }
        get() = (getProperty("list"))

    var max: String?
        set(value) {
            if (value == null) {
                removeProperty("max")
            } else {
                addProperty("max", value)
            }
        }
        get() = (getProperty("max"))

    var maxlength: Int?
        set(value) {
            if (value == null) {
                removeProperty("maxlength")
            } else {
                addProperty("maxlength", value)
            }
        }
        get() = (getProperty("maxlength"))

    var min: String?
        set(value) {
            if (value == null) {
                removeProperty("min")
            } else {
                addProperty("min", value)
            }
        }
        get() = (getProperty("min"))

    var multiple: Multiple?
        set(value) {
            if (value == null) {
                removeProperty("multiple")
            } else {
                addProperty("multiple", value.value)
            }
        }
        get() = Multiple.fromValue(getProperty("multiple"))

    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))

    var placeholder: String?
        set(value) {
            if (value == null) {
                removeProperty("placeholder")
            } else {
                addProperty("placeholder", value)
            }
        }
        get() = (getProperty("placeholder"))

    var readonly: Readonly?
        set(value) {
            if (value == null) {
                removeProperty("readonly")
            } else {
                addProperty("readonly", value.value)
            }
        }
        get() = Readonly.fromValue(getProperty("readonly"))

    var required: Required?
        set(value) {
            if (value == null) {
                removeProperty("required")
            } else {
                addProperty("required", value.value)
            }
        }
        get() = Required.fromValue(getProperty("required"))

    var size: Int?
        set(value) {
            if (value == null) {
                removeProperty("size")
            } else {
                addProperty("size", value)
            }
        }
        get() = (getProperty("size"))

    var src: String?
        set(value) {
            if (value == null) {
                removeProperty("src")
            } else {
                addProperty("src", value)
            }
        }
        get() = (getProperty("src"))

    var type: InputType?
        set(value) {
            if (value == null) {
                removeProperty("type")
            } else {
                addProperty("type", value.value)
            }
        }
        get() = InputType.fromValue(getProperty("type"))

    var value: String?
        set(value) {
            if (value == null) {
                removeProperty("value")
            } else {
                addProperty("value", value)
            }
        }
        get() = (getProperty("value"))

    var width: Int?
        set(value) {
            if (value == null) {
                removeProperty("width")
            } else {
                addProperty("width", value)
            }
        }
        get() = (getProperty("width"))



}