package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Html<MESSAGE> : org.iuv.core.HTML<MESSAGE>("html")
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var manifest: String?
        set(value) {
            if (value == null) {
                removeProperty("manifest")
            } else {
                addProperty("manifest", value)
            }
        }
        get() = (getProperty("manifest"))


    fun head(init: Head<MESSAGE>.() -> Unit) {
        element(Head(), init)
    }
    fun body(init: Body<MESSAGE>.() -> Unit) {
        element(Body(), init)
    }

}