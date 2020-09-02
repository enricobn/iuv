package org.iuv.core.html.groups

import org.iuv.core.HTMLElement
import org.iuv.core.html.elements.*

interface PhrasingContent<MESSAGE> : HTMLElement<MESSAGE>
 ,CommonPhrasingElements<MESSAGE>
 {
    fun a(init: PhrasingContentA<MESSAGE>.() -> Unit) {
        element(PhrasingContentA(), init)
    }
    fun audio(init: PhrasingContentAudio<MESSAGE>.() -> Unit) {
        element(PhrasingContentAudio(), init)
    }
    fun canvas(init: PhrasingContentCanvas<MESSAGE>.() -> Unit) {
        element(PhrasingContentCanvas(), init)
    }
    fun del(init: PhrasingContentDel<MESSAGE>.() -> Unit) {
        element(PhrasingContentDel(), init)
    }
    fun ins(init: PhrasingContentIns<MESSAGE>.() -> Unit) {
        element(PhrasingContentIns(), init)
    }
    fun map_(init: PhrasingContentMap<MESSAGE>.() -> Unit) {
        element(PhrasingContentMap(), init)
    }
    fun object_(init: PhrasingContentObject<MESSAGE>.() -> Unit) {
        element(PhrasingContentObject(), init)
    }
    fun video(init: PhrasingContentVideo<MESSAGE>.() -> Unit) {
        element(PhrasingContentVideo(), init)
    }
}