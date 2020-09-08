package org.iuv.core.html.attributegroups

import org.iuv.core.HTMLElementAttributes

interface CommonEventsGroup<MESSAGE> : HTMLElementAttributes<MESSAGE>
 
 {

    fun onabort(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("abort") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onabort(message: MESSAGE) {
        on("abort") { _: org.w3c.dom.events.Event -> message }
    }

    fun onblur(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("blur") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onblur(message: MESSAGE) {
        on("blur") { _: org.w3c.dom.events.Event -> message }
    }

    fun oncanplay(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("canplay") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun oncanplay(message: MESSAGE) {
        on("canplay") { _: org.w3c.dom.events.Event -> message }
    }

    fun oncanplaythrough(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("canplaythrough") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun oncanplaythrough(message: MESSAGE) {
        on("canplaythrough") { _: org.w3c.dom.events.Event -> message }
    }

    fun onchange(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("change") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onchange(message: MESSAGE) {
        on("change") { _: org.w3c.dom.events.Event -> message }
    }

    fun onclick(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("click") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onclick(message: MESSAGE) {
        on("click") { _: org.w3c.dom.events.Event -> message }
    }

    fun oncontextmenu(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("contextmenu") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun oncontextmenu(message: MESSAGE) {
        on("contextmenu") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondblclick(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("dblclick") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondblclick(message: MESSAGE) {
        on("dblclick") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondrag(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("drag") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondrag(message: MESSAGE) {
        on("drag") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragend(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("dragend") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondragend(message: MESSAGE) {
        on("dragend") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragenter(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("dragenter") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondragenter(message: MESSAGE) {
        on("dragenter") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragleave(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("dragleave") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondragleave(message: MESSAGE) {
        on("dragleave") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragover(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("dragover") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondragover(message: MESSAGE) {
        on("dragover") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondragstart(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("dragstart") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondragstart(message: MESSAGE) {
        on("dragstart") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondrop(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("drop") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondrop(message: MESSAGE) {
        on("drop") { _: org.w3c.dom.events.Event -> message }
    }

    fun ondurationchange(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("durationchange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ondurationchange(message: MESSAGE) {
        on("durationchange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onemptied(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("emptied") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onemptied(message: MESSAGE) {
        on("emptied") { _: org.w3c.dom.events.Event -> message }
    }

    fun onended(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("ended") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onended(message: MESSAGE) {
        on("ended") { _: org.w3c.dom.events.Event -> message }
    }

    fun onerror(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("error") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onerror(message: MESSAGE) {
        on("error") { _: org.w3c.dom.events.Event -> message }
    }

    fun onfocus(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("focus") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onfocus(message: MESSAGE) {
        on("focus") { _: org.w3c.dom.events.Event -> message }
    }

    fun oninput(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("input") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun oninput(message: MESSAGE) {
        on("input") { _: org.w3c.dom.events.Event -> message }
    }

    fun oninvalid(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("invalid") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun oninvalid(message: MESSAGE) {
        on("invalid") { _: org.w3c.dom.events.Event -> message }
    }

    fun onkeydown(handler: (org.w3c.dom.events.KeyboardEvent,dynamic) -> MESSAGE) {
        on("keydown") { event: org.w3c.dom.events.KeyboardEvent ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onkeydown(message: MESSAGE) {
        on("keydown") { _: org.w3c.dom.events.KeyboardEvent -> message }
    }

    fun onkeypress(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("keypress") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onkeypress(message: MESSAGE) {
        on("keypress") { _: org.w3c.dom.events.Event -> message }
    }

    fun onkeyup(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("keyup") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onkeyup(message: MESSAGE) {
        on("keyup") { _: org.w3c.dom.events.Event -> message }
    }

    fun onload(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("load") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onload(message: MESSAGE) {
        on("load") { _: org.w3c.dom.events.Event -> message }
    }

    fun onloadeddata(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("loadeddata") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onloadeddata(message: MESSAGE) {
        on("loadeddata") { _: org.w3c.dom.events.Event -> message }
    }

    fun onloadedmetadata(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("loadedmetadata") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onloadedmetadata(message: MESSAGE) {
        on("loadedmetadata") { _: org.w3c.dom.events.Event -> message }
    }

    fun onloadstart(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("loadstart") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onloadstart(message: MESSAGE) {
        on("loadstart") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmousedown(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("mousedown") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onmousedown(message: MESSAGE) {
        on("mousedown") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmousemove(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("mousemove") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onmousemove(message: MESSAGE) {
        on("mousemove") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmouseout(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("mouseout") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onmouseout(message: MESSAGE) {
        on("mouseout") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmouseover(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("mouseover") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onmouseover(message: MESSAGE) {
        on("mouseover") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmouseup(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("mouseup") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onmouseup(message: MESSAGE) {
        on("mouseup") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmousewheel(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("mousewheel") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onmousewheel(message: MESSAGE) {
        on("mousewheel") { _: org.w3c.dom.events.Event -> message }
    }

    fun onpause(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("pause") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onpause(message: MESSAGE) {
        on("pause") { _: org.w3c.dom.events.Event -> message }
    }

    fun onplay(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("play") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onplay(message: MESSAGE) {
        on("play") { _: org.w3c.dom.events.Event -> message }
    }

    fun onplaying(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("playing") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onplaying(message: MESSAGE) {
        on("playing") { _: org.w3c.dom.events.Event -> message }
    }

    fun onprogress(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("progress") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onprogress(message: MESSAGE) {
        on("progress") { _: org.w3c.dom.events.Event -> message }
    }

    fun onratechange(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("ratechange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onratechange(message: MESSAGE) {
        on("ratechange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onreadystatechange(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("readystatechange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onreadystatechange(message: MESSAGE) {
        on("readystatechange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onreset(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("reset") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onreset(message: MESSAGE) {
        on("reset") { _: org.w3c.dom.events.Event -> message }
    }

    fun onscroll(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("scroll") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onscroll(message: MESSAGE) {
        on("scroll") { _: org.w3c.dom.events.Event -> message }
    }

    fun onseeked(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("seeked") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onseeked(message: MESSAGE) {
        on("seeked") { _: org.w3c.dom.events.Event -> message }
    }

    fun onseeking(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("seeking") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onseeking(message: MESSAGE) {
        on("seeking") { _: org.w3c.dom.events.Event -> message }
    }

    fun onselect(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("select") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onselect(message: MESSAGE) {
        on("select") { _: org.w3c.dom.events.Event -> message }
    }

    fun onshow(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("show") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onshow(message: MESSAGE) {
        on("show") { _: org.w3c.dom.events.Event -> message }
    }

    fun onstalled(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("stalled") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onstalled(message: MESSAGE) {
        on("stalled") { _: org.w3c.dom.events.Event -> message }
    }

    fun onsubmit(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("submit") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onsubmit(message: MESSAGE) {
        on("submit") { _: org.w3c.dom.events.Event -> message }
    }

    fun onsuspend(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("suspend") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onsuspend(message: MESSAGE) {
        on("suspend") { _: org.w3c.dom.events.Event -> message }
    }

    fun ontimeupdate(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("timeupdate") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun ontimeupdate(message: MESSAGE) {
        on("timeupdate") { _: org.w3c.dom.events.Event -> message }
    }

    fun onvolumechange(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("volumechange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onvolumechange(message: MESSAGE) {
        on("volumechange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onwaiting(handler: (org.w3c.dom.events.Event,dynamic) -> MESSAGE) {
        on("waiting") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value)
        }
    }
    fun onwaiting(message: MESSAGE) {
        on("waiting") { _: org.w3c.dom.events.Event -> message }
    }

}