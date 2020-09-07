package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Label<MESSAGE> : HTML<MESSAGE>("label")
 ,PhrasingContentElement<MESSAGE>
 
 
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