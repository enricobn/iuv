package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface CommonEventsGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {

    fun onabort(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("abort") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onabort(message: MESSAGE) {
        on("abort") { _: org.w3c.dom.events.Event -> message }
    }

    fun onblur(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("blur") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onblur(message: MESSAGE) {
        on("blur") { _: org.w3c.dom.events.Event -> message }
    }

    fun oncanplay(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("canplay") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun oncanplay(message: MESSAGE) {
        on("canplay") { _: org.w3c.dom.events.Event -> message }
    }

    fun oncanplaythrough(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("canplaythrough") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun oncanplaythrough(message: MESSAGE) {
        on("canplaythrough") { _: org.w3c.dom.events.Event -> message }
    }

    fun onchange(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("change") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onchange(message: MESSAGE) {
        on("change") { _: org.w3c.dom.events.Event -> message }
    }

    fun onclick(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("click") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onclick(message: MESSAGE) {
        on("click") { _: org.w3c.dom.events.Event -> message }
    }

    fun oncontextmenu(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("contextmenu") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun oncontextmenu(message: MESSAGE) {
        on("contextmenu") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondblclick(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("dblclick") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondblclick(message: MESSAGE) {
        on("dblclick") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondrag(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("drag") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondrag(message: MESSAGE) {
        on("drag") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragend(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("dragend") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondragend(message: MESSAGE) {
        on("dragend") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragenter(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("dragenter") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondragenter(message: MESSAGE) {
        on("dragenter") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragleave(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("dragleave") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondragleave(message: MESSAGE) {
        on("dragleave") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragover(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("dragover") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondragover(message: MESSAGE) {
        on("dragover") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragstart(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("dragstart") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondragstart(message: MESSAGE) {
        on("dragstart") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondrop(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("drop") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondrop(message: MESSAGE) {
        on("drop") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondurationchange(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("durationchange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ondurationchange(message: MESSAGE) {
        on("durationchange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onemptied(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("emptied") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onemptied(message: MESSAGE) {
        on("emptied") { _: org.w3c.dom.events.Event -> message }
    }

    fun onended(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("ended") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onended(message: MESSAGE) {
        on("ended") { _: org.w3c.dom.events.Event -> message }
    }

    fun onerror(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("error") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onerror(message: MESSAGE) {
        on("error") { _: org.w3c.dom.events.Event -> message }
    }

    fun onfocus(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("focus") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onfocus(message: MESSAGE) {
        on("focus") { _: org.w3c.dom.events.Event -> message }
    }

    fun oninput(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("input") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun oninput(message: MESSAGE) {
        on("input") { _: org.w3c.dom.events.Event -> message }
    }

    fun oninvalid(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("invalid") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun oninvalid(message: MESSAGE) {
        on("invalid") { _: org.w3c.dom.events.Event -> message }
    }

    fun onkeydown(handler: (org.w3c.dom.events.KeyboardEvent,String) -> MESSAGE) {
        on("keydown") { event: org.w3c.dom.events.KeyboardEvent ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onkeydown(message: MESSAGE) {
        on("keydown") { _: org.w3c.dom.events.KeyboardEvent -> message }
    }

    fun onkeypress(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("keypress") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onkeypress(message: MESSAGE) {
        on("keypress") { _: org.w3c.dom.events.Event -> message }
    }

    fun onkeyup(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("keyup") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onkeyup(message: MESSAGE) {
        on("keyup") { _: org.w3c.dom.events.Event -> message }
    }

    fun onload(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("load") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onload(message: MESSAGE) {
        on("load") { _: org.w3c.dom.events.Event -> message }
    }

    fun onloadeddata(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("loadeddata") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onloadeddata(message: MESSAGE) {
        on("loadeddata") { _: org.w3c.dom.events.Event -> message }
    }

    fun onloadedmetadata(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("loadedmetadata") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onloadedmetadata(message: MESSAGE) {
        on("loadedmetadata") { _: org.w3c.dom.events.Event -> message }
    }

    fun onloadstart(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("loadstart") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onloadstart(message: MESSAGE) {
        on("loadstart") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmousedown(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("mousedown") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmousedown(message: MESSAGE) {
        on("mousedown") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmousemove(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("mousemove") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmousemove(message: MESSAGE) {
        on("mousemove") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmouseout(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("mouseout") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmouseout(message: MESSAGE) {
        on("mouseout") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmouseover(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("mouseover") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmouseover(message: MESSAGE) {
        on("mouseover") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmouseup(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("mouseup") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmouseup(message: MESSAGE) {
        on("mouseup") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmousewheel(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("mousewheel") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmousewheel(message: MESSAGE) {
        on("mousewheel") { _: org.w3c.dom.events.Event -> message }
    }

    fun onpause(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("pause") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onpause(message: MESSAGE) {
        on("pause") { _: org.w3c.dom.events.Event -> message }
    }

    fun onplay(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("play") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onplay(message: MESSAGE) {
        on("play") { _: org.w3c.dom.events.Event -> message }
    }

    fun onplaying(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("playing") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onplaying(message: MESSAGE) {
        on("playing") { _: org.w3c.dom.events.Event -> message }
    }

    fun onprogress(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("progress") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onprogress(message: MESSAGE) {
        on("progress") { _: org.w3c.dom.events.Event -> message }
    }

    fun onratechange(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("ratechange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onratechange(message: MESSAGE) {
        on("ratechange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onreadystatechange(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("readystatechange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onreadystatechange(message: MESSAGE) {
        on("readystatechange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onreset(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("reset") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onreset(message: MESSAGE) {
        on("reset") { _: org.w3c.dom.events.Event -> message }
    }

    fun onscroll(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("scroll") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onscroll(message: MESSAGE) {
        on("scroll") { _: org.w3c.dom.events.Event -> message }
    }

    fun onseeked(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("seeked") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onseeked(message: MESSAGE) {
        on("seeked") { _: org.w3c.dom.events.Event -> message }
    }

    fun onseeking(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("seeking") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onseeking(message: MESSAGE) {
        on("seeking") { _: org.w3c.dom.events.Event -> message }
    }

    fun onselect(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("select") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onselect(message: MESSAGE) {
        on("select") { _: org.w3c.dom.events.Event -> message }
    }

    fun onshow(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("show") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onshow(message: MESSAGE) {
        on("show") { _: org.w3c.dom.events.Event -> message }
    }

    fun onstalled(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("stalled") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onstalled(message: MESSAGE) {
        on("stalled") { _: org.w3c.dom.events.Event -> message }
    }

    fun onsubmit(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("submit") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onsubmit(message: MESSAGE) {
        on("submit") { _: org.w3c.dom.events.Event -> message }
    }

    fun onsuspend(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("suspend") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onsuspend(message: MESSAGE) {
        on("suspend") { _: org.w3c.dom.events.Event -> message }
    }

    fun ontimeupdate(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("timeupdate") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ontimeupdate(message: MESSAGE) {
        on("timeupdate") { _: org.w3c.dom.events.Event -> message }
    }

    fun onvolumechange(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("volumechange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onvolumechange(message: MESSAGE) {
        on("volumechange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onwaiting(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("waiting") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onwaiting(message: MESSAGE) {
        on("waiting") { _: org.w3c.dom.events.Event -> message }
    }

}