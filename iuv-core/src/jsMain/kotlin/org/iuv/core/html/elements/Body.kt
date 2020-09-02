package org.iuv.core.html.elements

class Body<MESSAGE> : org.iuv.core.HTML<MESSAGE>("body")
 
 
 {


    fun onafterprint(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("afterprint") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onafterprint(message: MESSAGE) {
        on("afterprint") { _: org.w3c.dom.events.Event -> message }
    }

    fun onbeforeprint(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("beforeprint") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onbeforeprint(message: MESSAGE) {
        on("beforeprint") { _: org.w3c.dom.events.Event -> message }
    }

    fun onbeforeunload(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("beforeunload") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onbeforeunload(message: MESSAGE) {
        on("beforeunload") { _: org.w3c.dom.events.Event -> message }
    }

    fun onhashchange(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("hashchange") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onhashchange(message: MESSAGE) {
        on("hashchange") { _: org.w3c.dom.events.Event -> message }
    }

    fun onmessage(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("message") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onmessage(message: MESSAGE) {
        on("message") { _: org.w3c.dom.events.Event -> message }
    }

    fun onoffline(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("offline") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onoffline(message: MESSAGE) {
        on("offline") { _: org.w3c.dom.events.Event -> message }
    }

    fun ononline(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("online") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun ononline(message: MESSAGE) {
        on("online") { _: org.w3c.dom.events.Event -> message }
    }

    fun onpopstate(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("popstate") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onpopstate(message: MESSAGE) {
        on("popstate") { _: org.w3c.dom.events.Event -> message }
    }

    fun onredo(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("redo") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onredo(message: MESSAGE) {
        on("redo") { _: org.w3c.dom.events.Event -> message }
    }

    fun onresize(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("resize") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onresize(message: MESSAGE) {
        on("resize") { _: org.w3c.dom.events.Event -> message }
    }

    fun onstorage(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("storage") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onstorage(message: MESSAGE) {
        on("storage") { _: org.w3c.dom.events.Event -> message }
    }

    fun onundo(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("undo") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onundo(message: MESSAGE) {
        on("undo") { _: org.w3c.dom.events.Event -> message }
    }

    fun onunload(handler: (org.w3c.dom.events.Event,String) -> MESSAGE) {
        on("unload") { event: org.w3c.dom.events.Event ->
            handler(event, event.target?.asDynamic().value as String)
        }
    }
    fun onunload(message: MESSAGE) {
        on("unload") { _: org.w3c.dom.events.Event -> message }
    }

}