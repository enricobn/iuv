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
                removeAttribute("type")
            } else {
                addAttribute("type", value.value)
            }
        }
        get() = MenuType.fromValue(getAttribute("type"))
    var label: String?
        set(value) {
            if (value == null) {
                removeAttribute("label")
            } else {
                addAttribute("label", value)
            }
        }
        get() = (getAttribute("label"))

    fun li(init: MenuLi<MESSAGE>.() -> Unit) {
        element(MenuLi(), init)
    }

}