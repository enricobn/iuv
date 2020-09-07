package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes
import org.iuv.core.html.enums.Autoplay
import org.iuv.core.html.enums.Controls
import org.iuv.core.html.enums.Loop
import org.iuv.core.html.enums.Preload

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

    var autoplay: Autoplay?
        set(value) {
            if (value == null) {
                removeProperty("autoplay")
            } else {
                addProperty("autoplay", value.value)
            }
        }
        get() = Autoplay.fromValue(getProperty("autoplay"))

    var preload: Preload?
        set(value) {
            if (value == null) {
                removeProperty("preload")
            } else {
                addProperty("preload", value.value)
            }
        }
        get() = Preload.fromValue(getProperty("preload"))

    var controls: Controls?
        set(value) {
            if (value == null) {
                removeProperty("controls")
            } else {
                addProperty("controls", value.value)
            }
        }
        get() = Controls.fromValue(getProperty("controls"))

    var loop: Loop?
        set(value) {
            if (value == null) {
                removeProperty("loop")
            } else {
                addProperty("loop", value.value)
            }
        }
        get() = Loop.fromValue(getProperty("loop"))

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