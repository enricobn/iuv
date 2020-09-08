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
                removeAttribute("src")
            } else {
                addAttribute("src", value)
            }
        }
        get() = (getAttribute("src"))
    var autoplay: Autoplay?
        set(value) {
            if (value == null) {
                removeAttribute("autoplay")
            } else {
                addAttribute("autoplay", value.value)
            }
        }
        get() = Autoplay.fromValue(getAttribute("autoplay"))
    var preload: Preload?
        set(value) {
            if (value == null) {
                removeAttribute("preload")
            } else {
                addAttribute("preload", value.value)
            }
        }
        get() = Preload.fromValue(getAttribute("preload"))
    var controls: Controls?
        set(value) {
            if (value == null) {
                removeAttribute("controls")
            } else {
                addAttribute("controls", value.value)
            }
        }
        get() = Controls.fromValue(getAttribute("controls"))
    var loop: Loop?
        set(value) {
            if (value == null) {
                removeAttribute("loop")
            } else {
                addAttribute("loop", value.value)
            }
        }
        get() = Loop.fromValue(getAttribute("loop"))
    var mediagroup: String?
        set(value) {
            if (value == null) {
                removeAttribute("mediagroup")
            } else {
                addAttribute("mediagroup", value)
            }
        }
        get() = (getAttribute("mediagroup"))

}