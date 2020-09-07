package org.iuv.core.html.elements
import org.iuv.core.HTML

open class Output<MESSAGE> : HTML<MESSAGE>("output")
 ,PhrasingContentElement<MESSAGE>
 
 
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