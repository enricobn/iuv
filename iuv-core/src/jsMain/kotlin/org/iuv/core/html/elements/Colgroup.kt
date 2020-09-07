package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Colgroup<MESSAGE> : HTML<MESSAGE>("colgroup")
 
 ,GlobalAttributeGroup<MESSAGE>
 
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