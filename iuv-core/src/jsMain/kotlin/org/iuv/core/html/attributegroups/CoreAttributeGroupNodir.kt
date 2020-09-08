package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.enums.Hidden
import org.iuv.core.html.enums.ImplicitBoolean

interface CoreAttributeGroupNodir<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var classes: String?
        set(value) {
            if (value == null) {
                removeAttribute("class")
            } else {
                addAttribute("class", value)
            }
        }
        get() = (getAttribute("class"))
    var contenteditable: ImplicitBoolean?
        set(value) {
            if (value == null) {
                removeAttribute("contenteditable")
            } else {
                addAttribute("contenteditable", value.value)
            }
        }
        get() = ImplicitBoolean.fromValue(getAttribute("contenteditable"))
    var contextmenu: String?
        set(value) {
            if (value == null) {
                removeAttribute("contextmenu")
            } else {
                addAttribute("contextmenu", value)
            }
        }
        get() = (getAttribute("contextmenu"))
    var draggable: Boolean?
        set(value) {
            if (value == null) {
                removeAttribute("draggable")
            } else {
                addAttribute("draggable", value)
            }
        }
        get() = (getAttribute("draggable"))
    var hidden: Hidden?
        set(value) {
            if (value == null) {
                removeAttribute("hidden")
            } else {
                addAttribute("hidden", value.value)
            }
        }
        get() = Hidden.fromValue(getAttribute("hidden"))
    var id: String?
        set(value) {
            if (value == null) {
                removeAttribute("id")
            } else {
                addAttribute("id", value)
            }
        }
        get() = (getAttribute("id"))
    var spellcheck: ImplicitBoolean?
        set(value) {
            if (value == null) {
                removeAttribute("spellcheck")
            } else {
                addAttribute("spellcheck", value.value)
            }
        }
        get() = ImplicitBoolean.fromValue(getAttribute("spellcheck"))
    var style: String?
        set(value) {
            if (value == null) {
                removeAttribute("style")
            } else {
                addAttribute("style", value)
            }
        }
        get() = (getAttribute("style"))
    var tabindex: Int?
        set(value) {
            if (value == null) {
                removeAttribute("tabindex")
            } else {
                addAttribute("tabindex", value)
            }
        }
        get() = (getAttribute("tabindex"))
    var title: String?
        set(value) {
            if (value == null) {
                removeAttribute("title")
            } else {
                addAttribute("title", value)
            }
        }
        get() = (getAttribute("title"))

}