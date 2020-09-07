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
                removeProperty("action")
            } else {
                addProperty("action", value)
            }
        }
        get() = (getProperty("action"))

    var method: Formmethod?
        set(value) {
            if (value == null) {
                removeProperty("method")
            } else {
                addProperty("method", value.value)
            }
        }
        get() = Formmethod.fromValue(getProperty("method"))

    var enctype: Formenctype?
        set(value) {
            if (value == null) {
                removeProperty("enctype")
            } else {
                addProperty("enctype", value.value)
            }
        }
        get() = Formenctype.fromValue(getProperty("enctype"))

    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))

    var novalidate: Formnovalidate?
        set(value) {
            if (value == null) {
                removeProperty("novalidate")
            } else {
                addProperty("novalidate", value.value)
            }
        }
        get() = Formnovalidate.fromValue(getProperty("novalidate"))

    var autocomplete: Autocomplete?
        set(value) {
            if (value == null) {
                removeProperty("autocomplete")
            } else {
                addProperty("autocomplete", value.value)
            }
        }
        get() = Autocomplete.fromValue(getProperty("autocomplete"))



}