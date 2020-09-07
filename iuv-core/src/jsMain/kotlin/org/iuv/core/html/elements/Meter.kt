package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Meter<MESSAGE> : HTML<MESSAGE>("meter")
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

    var min: Float?
        set(value) {
            if (value == null) {
                removeProperty("min")
            } else {
                addProperty("min", value)
            }
        }
        get() = (getProperty("min"))

    var low: Float?
        set(value) {
            if (value == null) {
                removeProperty("low")
            } else {
                addProperty("low", value)
            }
        }
        get() = (getProperty("low"))

    var high: Float?
        set(value) {
            if (value == null) {
                removeProperty("high")
            } else {
                addProperty("high", value)
            }
        }
        get() = (getProperty("high"))

    var max: Float?
        set(value) {
            if (value == null) {
                removeProperty("max")
            } else {
                addProperty("max", value)
            }
        }
        get() = (getProperty("max"))

    var optimum: Float?
        set(value) {
            if (value == null) {
                removeProperty("optimum")
            } else {
                addProperty("optimum", value)
            }
        }
        get() = (getProperty("optimum"))

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