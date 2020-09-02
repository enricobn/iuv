package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Autocomplete
import org.iuv.core.html.enums.Formmethod
import org.iuv.core.html.enums.InputType

class Input<MESSAGE> : org.iuv.core.HTML<MESSAGE>("input")
 ,GlobalAttributeGroup
 
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

    var autofocus: String?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value)
            }
        }
        get() = (getProperty("autofocus"))

    var checked: String?
        set(value) {
            if (value == null) {
                removeProperty("checked")
            } else {
                addProperty("checked", value)
            }
        }
        get() = (getProperty("checked"))

    var dirname: String?
        set(value) {
            if (value == null) {
                removeProperty("dirname")
            } else {
                addProperty("dirname", value)
            }
        }
        get() = (getProperty("dirname"))

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

    var formaction: String?
        set(value) {
            if (value == null) {
                removeProperty("formaction")
            } else {
                addProperty("formaction", value)
            }
        }
        get() = (getProperty("formaction"))

    var formenctype: String?
        set(value) {
            if (value == null) {
                removeProperty("formenctype")
            } else {
                addProperty("formenctype", value)
            }
        }
        get() = (getProperty("formenctype"))

    var formmethod: Formmethod?
        set(value) {
            if (value == null) {
                removeProperty("formmethod")
            } else {
                addProperty("formmethod", value.value)
            }
        }
        get() = Formmethod.fromValue(getProperty("formmethod"))

    var formnovalidate: String?
        set(value) {
            if (value == null) {
                removeProperty("formnovalidate")
            } else {
                addProperty("formnovalidate", value)
            }
        }
        get() = (getProperty("formnovalidate"))

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

    var multiple: String?
        set(value) {
            if (value == null) {
                removeProperty("multiple")
            } else {
                addProperty("multiple", value)
            }
        }
        get() = (getProperty("multiple"))

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

    var readonly: String?
        set(value) {
            if (value == null) {
                removeProperty("readonly")
            } else {
                addProperty("readonly", value)
            }
        }
        get() = (getProperty("readonly"))

    var required: String?
        set(value) {
            if (value == null) {
                removeProperty("required")
            } else {
                addProperty("required", value)
            }
        }
        get() = (getProperty("required"))

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