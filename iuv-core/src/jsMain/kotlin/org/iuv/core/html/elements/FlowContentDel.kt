package org.iuv.core.html.elements

class FlowContentDel<MESSAGE> : org.iuv.core.HTML<MESSAGE>("del")
 
 
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