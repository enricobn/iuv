package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

open class Table<MESSAGE> : HTML<MESSAGE>("table")
 
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
    fun tbody(init: Tbody<MESSAGE>.() -> Unit) {
        element(Tbody(), init)
    }
    fun tr(init: Tr<MESSAGE>.() -> Unit) {
        element(Tr(), init)
    }
    fun tfoot(init: Tfoot<MESSAGE>.() -> Unit) {
        element(Tfoot(), init)
    }

}