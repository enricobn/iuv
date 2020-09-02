package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface AudioAttributeGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
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