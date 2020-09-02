package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Table<MESSAGE> : org.iuv.core.HTML<MESSAGE>("table")
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var border: String?
        set(value) {
            if (value == null) {
                removeProperty("border")
            } else {
                addProperty("border", value)
            }
        }
        get() = (getProperty("border"))


    fun caption(init: Caption<MESSAGE>.() -> Unit) {
        element(Caption(), init)
    }
    fun colgroup(init: Colgroup<MESSAGE>.() -> Unit) {
        element(Colgroup(), init)
    }
    fun thead(init: Thead<MESSAGE>.() -> Unit) {
        element(Thead(), init)
    }

}