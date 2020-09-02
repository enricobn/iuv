package org.iuv.core.html.elements

class FlowContentIns<MESSAGE> : org.iuv.core.HTML<MESSAGE>("ins")
 
 
 {
    var cite: String?
        set(value) {
            if (value == null) {
                removeProperty("cite")
            } else {
                addProperty("cite", value)
            }
        }
        get() = (getProperty("cite"))



}