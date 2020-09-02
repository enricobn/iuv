package org.iuv.core.html.elements

class PhrasingContentDel<MESSAGE> : org.iuv.core.HTML<MESSAGE>("del")
 
 
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