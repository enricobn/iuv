package org.iuv.core.html.elements

class Blockquote<MESSAGE> : org.iuv.core.HTML<MESSAGE>("blockquote")
 
 
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