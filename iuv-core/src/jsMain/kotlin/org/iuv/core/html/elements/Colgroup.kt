package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Colgroup<MESSAGE> : org.iuv.core.HTML<MESSAGE>("colgroup")
 ,GlobalAttributeGroup
 
 {
    var span: Int?
        set(value) {
            if (value == null) {
                removeProperty("span")
            } else {
                addProperty("span", value)
            }
        }
        get() = (getProperty("span"))


    fun col(init: Col<MESSAGE>.() -> Unit) {
        element(Col(), init)
    }
}