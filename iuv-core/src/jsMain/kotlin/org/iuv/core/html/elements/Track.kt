package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.TrackKind

class Track<MESSAGE> : org.iuv.core.HTML<MESSAGE>("track")
 ,GlobalAttributeGroup
 
 {
    var kind: TrackKind?
        set(value) {
            if (value == null) {
                removeProperty("kind")
            } else {
                addProperty("kind", value.value)
            }
        }
        get() = TrackKind.fromValue(getProperty("kind"))

    var src: String?
        set(value) {
            if (value == null) {
                removeProperty("src")
            } else {
                addProperty("src", value)
            }
        }
        get() = (getProperty("src"))

    var label: String?
        set(value) {
            if (value == null) {
                removeProperty("label")
            } else {
                addProperty("label", value)
            }
        }
        get() = (getProperty("label"))

    var default: String?
        set(value) {
            if (value == null) {
                removeProperty("default")
            } else {
                addProperty("default", value)
            }
        }
        get() = (getProperty("default"))


}