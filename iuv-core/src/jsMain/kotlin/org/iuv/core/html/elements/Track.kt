package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Default
import org.iuv.core.html.enums.TrackKind

open class Track<MESSAGE> : HTML<MESSAGE>("track")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var kind: TrackKind?
        set(value) {
            if (value == null) {
                removeAttribute("kind")
            } else {
                addAttribute("kind", value.value)
            }
        }
        get() = TrackKind.fromValue(getAttribute("kind"))
    var src: String?
        set(value) {
            if (value == null) {
                removeAttribute("src")
            } else {
                addAttribute("src", value)
            }
        }
        get() = (getAttribute("src"))
    var label: String?
        set(value) {
            if (value == null) {
                removeAttribute("label")
            } else {
                addAttribute("label", value)
            }
        }
        get() = (getAttribute("label"))
    var default: Default?
        set(value) {
            if (value == null) {
                removeAttribute("default")
            } else {
                addAttribute("default", value.value)
            }
        }
        get() = Default.fromValue(getAttribute("default"))


}