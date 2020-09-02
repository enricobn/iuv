package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.MenuType

class Menu<MESSAGE> : org.iuv.core.HTML<MESSAGE>("menu")
 ,GlobalAttributeGroup
 ,org.iuv.core.html.groups.FlowContent<MESSAGE>
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