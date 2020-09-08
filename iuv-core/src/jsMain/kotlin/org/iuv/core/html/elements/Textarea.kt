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
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))
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
    var readonly: Readonly?
        set(value) {
            if (value == null) {
                removeAttribute("readonly")
            } else {
                addAttribute("readonly", value.value)
            }
        }
        get() = Readonly.fromValue(getAttribute("readonly"))
    var maxlength: Int?
        set(value) {
            if (value == null) {
                removeAttribute("maxlength")
            } else {
                addAttribute("maxlength", value)
            }
        }
        get() = (getAttribute("maxlength"))
    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeAttribute("autofocus")
            } else {
                addAttribute("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getAttribute("autofocus"))
    var required: Required?
        set(value) {
            if (value == null) {
                removeAttribute("required")
            } else {
                addAttribute("required", value.value)
            }
        }
        get() = Required.fromValue(getAttribute("required"))
    var placeholder: String?
        set(value) {
            if (value == null) {
                removeAttribute("placeholder")
            } else {
                addAttribute("placeholder", value)
            }
        }
        get() = (getAttribute("placeholder"))
    var rows: Int?
        set(value) {
            if (value == null) {
                removeAttribute("rows")
            } else {
                addAttribute("rows", value)
            }
        }
        get() = (getAttribute("rows"))
    var cols: Int?
        set(value) {
            if (value == null) {
                removeAttribute("cols")
            } else {
                addAttribute("cols", value)
            }
        }
        get() = (getAttribute("cols"))
    var wrap: Wrap?
        set(value) {
            if (value == null) {
                removeAttribute("wrap")
            } else {
                addAttribute("wrap", value.value)
            }
        }
        get() = Wrap.fromValue(getAttribute("wrap"))


}