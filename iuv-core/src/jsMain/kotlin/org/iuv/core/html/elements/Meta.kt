package org.iuv.core.html.elements
import org.iuv.core.HTML
import org.iuv.core.html.attributegroups.GlobalAttributeGroup
import org.iuv.core.html.enums.HttpEquiv

open class Meta<MESSAGE> : HTML<MESSAGE>("meta")
 
 ,GlobalAttributeGroup<MESSAGE>
 
 {
    var httpequiv: HttpEquiv?
        set(value) {
            if (value == null) {
                removeAttribute("http-equiv")
            } else {
                addAttribute("http-equiv", value.value)
            }
        }
        get() = HttpEquiv.fromValue(getAttribute("http-equiv"))
    var name: String?
        set(value) {
            if (value == null) {
                removeAttribute("name")
            } else {
                addAttribute("name", value)
            }
        }
        get() = (getAttribute("name"))
    var content: String?
        set(value) {
            if (value == null) {
                removeAttribute("content")
            } else {
                addAttribute("content", value)
            }
        }
        get() = (getAttribute("content"))
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