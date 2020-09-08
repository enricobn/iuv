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
                removeAttribute("challenge")
            } else {
                addAttribute("challenge", value)
            }
        }
        get() = (getAttribute("challenge"))
    var keytype: Keytype?
        set(value) {
            if (value == null) {
                removeAttribute("keytype")
            } else {
                addAttribute("keytype", value.value)
            }
        }
        get() = Keytype.fromValue(getAttribute("keytype"))
    var autofocus: Autofocus?
        set(value) {
            if (value == null) {
                removeAttribute("autofocus")
            } else {
                addAttribute("autofocus", value.value)
            }
        }
        get() = Autofocus.fromValue(getAttribute("autofocus"))
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


}