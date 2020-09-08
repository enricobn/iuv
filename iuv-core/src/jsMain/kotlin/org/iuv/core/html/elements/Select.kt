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
    var size: Int?
        set(value) {
            if (value == null) {
                removeAttribute("size")
            } else {
                addAttribute("size", value)
            }
        }
        get() = (getAttribute("size"))
    var multiple: Multiple?
        set(value) {
            if (value == null) {
                removeAttribute("multiple")
            } else {
                addAttribute("multiple", value.value)
            }
        }
        get() = Multiple.fromValue(getAttribute("multiple"))
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

    fun option(init: Option<MESSAGE>.() -> Unit) {
        element(Option(), init)
    }
    fun optgroup(init: Optgroup<MESSAGE>.() -> Unit) {
        element(Optgroup(), init)
    }

}