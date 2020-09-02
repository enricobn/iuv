package org.iuv.core.html.elements
import org.iuv.core.html.enums.ButtonType
import org.iuv.core.html.enums.Formmethod

class Button<MESSAGE> : org.iuv.core.HTML<MESSAGE>("button")
 
 
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

    var autofocus: String?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value)
            }
        }
        get() = (getProperty("autofocus"))

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


}