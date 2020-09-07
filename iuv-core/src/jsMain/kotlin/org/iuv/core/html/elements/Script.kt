package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.Async
import org.iuv.core.html.enums.Defer

open class Script<MESSAGE> : HTML<MESSAGE>("script")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var src: String?
        set(value) {
            if (value == null) {
                removeProperty("src")
            } else {
                addProperty("src", value)
            }
        }
        get() = (getProperty("src"))

    var defer: Defer?
        set(value) {
            if (value == null) {
                removeProperty("defer")
            } else {
                addProperty("defer", value.value)
            }
        }
        get() = Defer.fromValue(getProperty("defer"))

    var async: Async?
        set(value) {
            if (value == null) {
                removeProperty("async")
            } else {
                addProperty("async", value.value)
            }
        }
        get() = Async.fromValue(getProperty("async"))

    var charset: String?
        set(value) {
            if (value == null) {
                removeProperty("charset")
            } else {
                addProperty("charset", value)
            }
        }
        get() = (getProperty("charset"))



}