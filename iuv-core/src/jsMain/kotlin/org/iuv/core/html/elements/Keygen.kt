package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Keytype

class Keygen<MESSAGE> : org.iuv.core.HTML<MESSAGE>("keygen")
 ,GlobalAttributeGroup
 
 {
    var challenge: String?
        set(value) {
            if (value == null) {
                removeProperty("challenge")
            } else {
                addProperty("challenge", value)
            }
        }
        get() = (getProperty("challenge"))

    var keytype: Keytype?
        set(value) {
            if (value == null) {
                removeProperty("keytype")
            } else {
                addProperty("keytype", value.value)
            }
        }
        get() = Keytype.fromValue(getProperty("keytype"))

    var autofocus: String?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value)
            }
        }
        get() = (getProperty("autofocus"))

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


}