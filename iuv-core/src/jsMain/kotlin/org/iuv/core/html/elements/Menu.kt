package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.MenuType
import org.iuv.core.html.groups.FlowContent

open class Menu<MESSAGE> : HTML<MESSAGE>("menu")
 
 ,GlobalAttributeGroup<MESSAGE>
 ,FlowContent<MESSAGE>
 {
    var type: MenuType?
        set(value) {
            if (value == null) {
                removeProperty("type")
            } else {
                addProperty("type", value.value)
            }
        }
        get() = MenuType.fromValue(getProperty("type"))

    var label: String?
        set(value) {
            if (value == null) {
                removeProperty("label")
            } else {
                addProperty("label", value)
            }
        }
        get() = (getProperty("label"))


    fun li(init: MenuLi<MESSAGE>.() -> Unit) {
        element(MenuLi(), init)
    }

}