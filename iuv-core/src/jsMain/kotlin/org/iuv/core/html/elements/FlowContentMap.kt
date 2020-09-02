package org.iuv.core.html.elements

class FlowContentMap<MESSAGE> : org.iuv.core.HTML<MESSAGE>("map")
 
 
 {
    var name: String?
        set(value) {
            if (value == null) {
                removeProperty("name")
            } else {
                addProperty("name", value)
            }
        }
        get() = (getProperty("name"))


}