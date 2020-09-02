package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface VideoAttributeGroup : HTMLElementAttributes
 
 {
    var src: String?
        set(value) {
            if (value == null) {
                removeProperty("src")
            } else {
                addProperty("src", value)
            }
        }
        get() = (getProperty("src"))

    var autoplay: String?
        set(value) {
            if (value == null) {
                removeProperty("autoplay")
            } else {
                addProperty("autoplay", value)
            }
        }
        get() = (getProperty("autoplay"))

    var preload: String?
        set(value) {
            if (value == null) {
                removeProperty("preload")
            } else {
                addProperty("preload", value)
            }
        }
        get() = (getProperty("preload"))

    var controls: String?
        set(value) {
            if (value == null) {
                removeProperty("controls")
            } else {
                addProperty("controls", value)
            }
        }
        get() = (getProperty("controls"))

    var loop: String?
        set(value) {
            if (value == null) {
                removeProperty("loop")
            } else {
                addProperty("loop", value)
            }
        }
        get() = (getProperty("loop"))

    var poster: String?
        set(value) {
            if (value == null) {
                removeProperty("poster")
            } else {
                addProperty("poster", value)
            }
        }
        get() = (getProperty("poster"))

    var height: Int?
        set(value) {
            if (value == null) {
                removeProperty("height")
            } else {
                addProperty("height", value)
            }
        }
        get() = (getProperty("height"))

    var width: Int?
        set(value) {
            if (value == null) {
                removeProperty("width")
            } else {
                addProperty("width", value)
            }
        }
        get() = (getProperty("width"))

    var muted: String?
        set(value) {
            if (value == null) {
                removeProperty("muted")
            } else {
                addProperty("muted", value)
            }
        }
        get() = (getProperty("muted"))

    var mediagroup: String?
        set(value) {
            if (value == null) {
                removeProperty("mediagroup")
            } else {
                addProperty("mediagroup", value)
            }
        }
        get() = (getProperty("mediagroup"))

}