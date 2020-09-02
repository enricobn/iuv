package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface CoreAttributeGroupNodir<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {
    var classes: String?
        set(value) {
            if (value == null) {
                removeProperty("class")
            } else {
                addProperty("class", value)
            }
        }
        get() = (getProperty("class"))

    var contenteditable: String?
        set(value) {
            if (value == null) {
                removeProperty("contenteditable")
            } else {
                addProperty("contenteditable", value)
            }
        }
        get() = (getProperty("contenteditable"))

    var contextmenu: String?
        set(value) {
            if (value == null) {
                removeProperty("contextmenu")
            } else {
                addProperty("contextmenu", value)
            }
        }
        get() = (getProperty("contextmenu"))

    var draggable: Boolean?
        set(value) {
            if (value == null) {
                removeProperty("draggable")
            } else {
                addProperty("draggable", value)
            }
        }
        get() = (getProperty("draggable"))

    var hidden: String?
        set(value) {
            if (value == null) {
                removeProperty("hidden")
            } else {
                addProperty("hidden", value)
            }
        }
        get() = (getProperty("hidden"))

    var id: String?
        set(value) {
            if (value == null) {
                removeProperty("id")
            } else {
                addProperty("id", value)
            }
        }
        get() = (getProperty("id"))

    var spellcheck: String?
        set(value) {
            if (value == null) {
                removeProperty("spellcheck")
            } else {
                addProperty("spellcheck", value)
            }
        }
        get() = (getProperty("spellcheck"))

    var style: String?
        set(value) {
            if (value == null) {
                removeProperty("style")
            } else {
                addProperty("style", value)
            }
        }
        get() = (getProperty("style"))

    var tabindex: Int?
        set(value) {
            if (value == null) {
                removeProperty("tabindex")
            } else {
                addProperty("tabindex", value)
            }
        }
        get() = (getProperty("tabindex"))

    var title: String?
        set(value) {
            if (value == null) {
                removeProperty("title")
            } else {
                addProperty("title", value)
            }
        }
        get() = (getProperty("title"))


}