package org.iuv.core.html.elements

class OlLi<MESSAGE> : org.iuv.core.HTML<MESSAGE>("li")
 
 
 {
    var value: Int?
        set(value) {
            if (value == null) {
                removeProperty("value")
            } else {
                addProperty("value", value)
            }
        }
        get() = (getProperty("value"))



}