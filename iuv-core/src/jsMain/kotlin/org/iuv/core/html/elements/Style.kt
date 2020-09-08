package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Scoped

open class Style<MESSAGE> : HTML<MESSAGE>("style")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var scoped: Scoped?
        set(value) {
            if (value == null) {
                removeAttribute("scoped")
            } else {
                addAttribute("scoped", value.value)
            }
        }
        get() = Scoped.fromValue(getAttribute("scoped"))


}