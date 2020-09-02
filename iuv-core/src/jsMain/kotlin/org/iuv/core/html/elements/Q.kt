package org.iuv.core.html.elements

class Q<MESSAGE> : org.iuv.core.HTML<MESSAGE>("q")
 
 
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