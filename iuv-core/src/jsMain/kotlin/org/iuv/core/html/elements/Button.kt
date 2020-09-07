package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.enums.*

open class Button<MESSAGE> : HTML<MESSAGE>("button")
 ,PhrasingContentElement<MESSAGE>
 
 
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

    var type: ButtonType?
        set(value) {
            if (value == null) {
                removeProperty("type")
            } else {
                addProperty("type", value.value)
            }
        }
        get() = ButtonType.fromValue(getProperty("type"))

    var value: String?
        set(value) {
            if (value == null) {
                removeProperty("value")
            } else {
                addProperty("value", value)
            }
        }
        get() = (getProperty("value"))

    var formaction: String?
        set(value) {
            if (value == null) {
                removeProperty("formaction")
            } else {
                addProperty("formaction", value)
            }
        }
        get() = (getProperty("formaction"))

    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getProperty("autofocus"))

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



}