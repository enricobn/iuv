package org.iuv.core

import org.w3c.xhr.XMLHttpRequest

// TODO adjust url with timestamp to bypass the cache:
// oReq.open("GET", url + ((/\?/).test(url) ? "&" : "?") + (new Date()).getTime());
// TODO handle failure && UNSENT state
class GetAsync<in J, out MESSAGE>(private val url: String, private val handler: (J) -> MESSAGE) : Cmd<MESSAGE> {

    override fun run(messageBus: MessageBus<MESSAGE>) {
        val request = XMLHttpRequest()
        request.onreadystatechange = { _ ->
            if (request.readyState == XMLHttpRequest.DONE && request.status.toInt() == 200) {
                val response = kotlin.js.JSON.parse<J>(request.responseText)
                messageBus.send(handler(response))
            }
        }
        request.open("get", url, true)
        request.send()
    }

}

interface UV<MODEL, MESSAGE> {

    fun subscriptions(model: MODEL) : Sub<MESSAGE> = Sub.none()

    fun update(message: MESSAGE, model: MODEL) : Pair<MODEL, Cmd<MESSAGE>>

    fun view(model: MODEL): HTML<MESSAGE>

    fun html(init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
        val html = HTML<MESSAGE>("div")
        init(html)
        return html
    }

    fun sendMessage(msg: MESSAGE) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            messageBus.send(msg)
        }
    }

}

