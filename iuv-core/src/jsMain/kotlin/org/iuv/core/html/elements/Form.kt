package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.enums.Autocomplete
import org.iuv.core.html.enums.Formenctype
import org.iuv.core.html.enums.Formmethod
import org.iuv.core.html.enums.Formnovalidate

open class Form<MESSAGE> : HTML<MESSAGE>("form")
 ,FlowContentElement<MESSAGE>
 
 
 {
    var action: String?
        set(value) {
            if (value == null) {
                removeAttribute("action")
            } else {
                addAttribute("action", value)
            }
        }
        get() = (getAttribute("action"))
    var method: Formmethod?
        set(value) {
            if (value == null) {
                removeAttribute("method")
            } else {
                addAttribute("method", value.value)
            }
        }
        get() = Formmethod.fromValue(getAttribute("method"))
    var enctype: Formenctype?
        set(value) {
            if (value == null) {
                removeAttribute("enctype")
            } else {
                addAttribute("enctype", value.value)
            }
        }
        get() = Formenctype.fromValue(getAttribute("enctype"))
    var name: String?
        set(value) {
            if (value == null) {
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))
    var novalidate: Formnovalidate?
        set(value) {
            if (value == null) {
                removeAttribute("novalidate")
            } else {
                addAttribute("novalidate", value.value)
            }
        }
        get() = Formnovalidate.fromValue(getAttribute("novalidate"))
    var autocomplete: Autocomplete?
        set(value) {
            if (value == null) {
                removeAttribute("autocomplete")
            } else {
                addAttribute("autocomplete", value.value)
            }
        }
        get() = Autocomplete.fromValue(getAttribute("autocomplete"))


}