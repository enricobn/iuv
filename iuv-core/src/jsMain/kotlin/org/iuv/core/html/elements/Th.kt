package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.ThAttributeGroup
import org.iuv.core.html.enums.Scope

class Th<MESSAGE> : org.iuv.core.HTML<MESSAGE>("th")
 ,ThAttributeGroup
 
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