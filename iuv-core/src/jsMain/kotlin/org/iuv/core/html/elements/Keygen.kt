package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Autofocus
import org.iuv.core.html.enums.Disabled
import org.iuv.core.html.enums.Keytype

open class Keygen<MESSAGE> : HTML<MESSAGE>("keygen")
 
 ,GlobalAttributeGroup<MESSAGE>
 
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

    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeProperty("autofocus")
            } else {
                addProperty("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getProperty("autofocus"))

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



}