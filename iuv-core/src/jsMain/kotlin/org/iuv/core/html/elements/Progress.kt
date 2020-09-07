package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Progress<MESSAGE> : HTML<MESSAGE>("progress")
 ,PhrasingContentElement<MESSAGE>
 
 
 {
    var value: Float?
        set(value) {
            if (value == null) {
                removeProperty("value")
            } else {
                addProperty("value", value)
            }
        }
        get() = (getProperty("value"))

    var max: Float?
        set(value) {
            if (value == null) {
                removeProperty("max")
            } else {
                addProperty("max", value)
            }
        }
        get() = (getProperty("max"))

    var form: String?
        set(value) {
            if (value == null) {
                removeProperty("form")
            } else {
                addProperty("form", value)
            }
        }
        get() = (getProperty("form"))



}