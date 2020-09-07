package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.ThAttributeGroup
import org.iuv.core.html.enums.Scope

open class Th<MESSAGE> : HTML<MESSAGE>("th")
 ,FlowContentElement<MESSAGE>
 ,ThAttributeGroup<MESSAGE>
 
 {
    var scope: Scope?
        set(value) {
            if (value == null) {
                removeProperty("scope")
            } else {
                addProperty("scope", value.value)
            }
        }
        get() = Scope.fromValue(getProperty("scope"))



}