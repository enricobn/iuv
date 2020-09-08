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
                removeAttribute("src")
            } else {
                addAttribute("src", value)
            }
        }
        get() = (getAttribute("src"))
    var defer: Defer?
        set(value) {
            if (value == null) {
                removeAttribute("defer")
            } else {
                addAttribute("defer", value.value)
            }
        }
        get() = Defer.fromValue(getAttribute("defer"))
    var async: Async?
        set(value) {
            if (value == null) {
                removeAttribute("async")
            } else {
                addAttribute("async", value.value)
            }
        }
        get() = Async.fromValue(getAttribute("async"))
    var charset: String?
        set(value) {
            if (value == null) {
                removeAttribute("charset")
            } else {
                addAttribute("charset", value)
            }
        }
        get() = (getAttribute("charset"))


}