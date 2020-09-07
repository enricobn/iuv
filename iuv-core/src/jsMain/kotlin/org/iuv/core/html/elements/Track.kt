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

    var default: Default?
        set(value) {
            if (value == null) {
                removeProperty("default")
            } else {
                addProperty("default", value.value)
            }
        }
        get() = Default.fromValue(getProperty("default"))



}