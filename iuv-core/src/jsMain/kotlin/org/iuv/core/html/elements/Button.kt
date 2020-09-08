package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.enums.*

open class Button<MESSAGE> : HTML<MESSAGE>("button")
 ,PhrasingContentElement<MESSAGE>
 
 
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
    var type: ButtonType?
        set(value) {
            if (value == null) {
                removeAttribute("type")
            } else {
                addAttribute("type", value.value)
            }
        }
        get() = ButtonType.fromValue(getAttribute("type"))
    var value: String?
        set(value) {
            if (value == null) {
                removeAttribute("value")
            } else {
                addAttribute("value", value)
            }
        }
        get() = (getAttribute("value"))
    var formaction: String?
        set(value) {
            if (value == null) {
                removeAttribute("formaction")
            } else {
                addAttribute("formaction", value)
            }
        }
        get() = (getAttribute("formaction"))
    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeAttribute("autofocus")
            } else {
                addAttribute("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getAttribute("autofocus"))
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


}