package org.iuv.core.html.elements

class Label<MESSAGE> : org.iuv.core.HTML<MESSAGE>("label")
 
 
 {
    var for_: String?
        set(value) {
            if (value == null) {
                removeProperty("for")
            } else {
                addProperty("for", value)
            }
        }
        get() = (getProperty("for"))

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