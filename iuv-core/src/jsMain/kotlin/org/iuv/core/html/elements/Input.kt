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
                removeAttribute("accept")
            } else {
                addAttribute("accept", value)
            }
        }
        get() = (getAttribute("accept"))
    var alt: String?
        set(value) {
            if (value == null) {
                removeAttribute("alt")
            } else {
                addAttribute("alt", value)
            }
        }
        get() = (getAttribute("alt"))
    var autocomplete: Autocomplete?
        set(value) {
            if (value == null) {
                removeAttribute("autocomplete")
            } else {
                addAttribute("autocomplete", value.value)
            }
        }
        get() = Autocomplete.fromValue(getAttribute("autocomplete"))
    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeAttribute("autofocus")
            } else {
                addAttribute("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getAttribute("autofocus"))
    var checked: Checked?
        set(value) {
            if (value == null) {
                removeAttribute("checked")
            } else {
                addAttribute("checked", value.value)
            }
        }
        get() = Checked.fromValue(getAttribute("checked"))
    var dirname: String?
        set(value) {
            if (value == null) {
                removeAttribute("dirname")
            } else {
                addAttribute("dirname", value)
            }
        }
        get() = (getAttribute("dirname"))
    var disabled: Disabled?
        set(value) {
            if (value == null) {
                removeAttribute("disabled")
            } else {
                addAttribute("disabled", value.value)
            }
        }
        get() = Disabled.fromValue(getAttribute("disabled"))
    var form: String?
        set(value) {
            if (value == null) {
                removeAttribute("form")
            } else {
                addAttribute("form", value)
            }
        }
        get() = (getAttribute("form"))
    var formaction: String?
        set(value) {
            if (value == null) {
                removeAttribute("formaction")
            } else {
                addAttribute("formaction", value)
            }
        }
        get() = (getAttribute("formaction"))
    var formenctype: Formenctype?
        set(value) {
            if (value == null) {
                removeAttribute("formenctype")
            } else {
                addAttribute("formenctype", value.value)
            }
        }
        get() = Formenctype.fromValue(getAttribute("formenctype"))
    var formmethod: Formmethod?
        set(value) {
            if (value == null) {
                removeAttribute("formmethod")
            } else {
                addAttribute("formmethod", value.value)
            }
        }
        get() = Formmethod.fromValue(getAttribute("formmethod"))
    var formnovalidate: Formnovalidate?
        set(value) {
            if (value == null) {
                removeAttribute("formnovalidate")
            } else {
                addAttribute("formnovalidate", value.value)
            }
        }
        get() = Formnovalidate.fromValue(getAttribute("formnovalidate"))
    var height: Int?
        set(value) {
            if (value == null) {
                removeAttribute("height")
            } else {
                addAttribute("height", value)
            }
        }
        get() = (getAttribute("height"))
    var list: String?
        set(value) {
            if (value == null) {
                removeAttribute("list")
            } else {
                addAttribute("list", value)
            }
        }
        get() = (getAttribute("list"))
    var max: String?
        set(value) {
            if (value == null) {
                removeAttribute("max")
            } else {
                addAttribute("max", value)
            }
        }
        get() = (getAttribute("max"))
    var maxlength: Int?
        set(value) {
            if (value == null) {
                removeAttribute("maxlength")
            } else {
                addAttribute("maxlength", value)
            }
        }
        get() = (getAttribute("maxlength"))
    var min: String?
        set(value) {
            if (value == null) {
                removeAttribute("min")
            } else {
                addAttribute("min", value)
            }
        }
        get() = (getAttribute("min"))
    var multiple: Multiple?
        set(value) {
            if (value == null) {
                removeAttribute("multiple")
            } else {
                addAttribute("multiple", value.value)
            }
        }
        get() = Multiple.fromValue(getAttribute("multiple"))
    var name: String?
        set(value) {
            if (value == null) {
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))
    var placeholder: String?
        set(value) {
            if (value == null) {
                removeAttribute("placeholder")
            } else {
                addAttribute("placeholder", value)
            }
        }
        get() = (getAttribute("placeholder"))
    var readonly: Readonly?
        set(value) {
            if (value == null) {
                removeAttribute("readonly")
            } else {
                addAttribute("readonly", value.value)
            }
        }
        get() = Readonly.fromValue(getAttribute("readonly"))
    var required: Required?
        set(value) {
            if (value == null) {
                removeAttribute("required")
            } else {
                addAttribute("required", value.value)
            }
        }
        get() = Required.fromValue(getAttribute("required"))
    var size: Int?
        set(value) {
            if (value == null) {
                removeAttribute("size")
            } else {
                addAttribute("size", value)
            }
        }
        get() = (getAttribute("size"))
    var src: String?
        set(value) {
            if (value == null) {
                removeAttribute("src")
            } else {
                addAttribute("src", value)
            }
        }
        get() = (getAttribute("src"))
    var type: InputType?
        set(value) {
            if (value == null) {
                removeAttribute("type")
            } else {
                addAttribute("type", value.value)
            }
        }
        get() = InputType.fromValue(getAttribute("type"))
    var value: String?
        set(value) {
            if (value == null) {
                removeAttribute("value")
            } else {
                addAttribute("value", value)
            }
        }
        get() = (getAttribute("value"))
    var width: Int?
        set(value) {
            if (value == null) {
                removeAttribute("width")
            } else {
                addAttribute("width", value)
            }
        }
        get() = (getAttribute("width"))


}