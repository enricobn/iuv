package org.iuv.core

import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

class GetAsync<out RESULT,MESSAGE>(private val url: String) : Task<RESULT,String,MESSAGE>() {

    override fun start(onSuccess: (RESULT) -> Unit, onError: (String) -> Unit) {
        val request = XMLHttpRequest()
        request.onreadystatechange = { _ ->
            when(request.readyState) {
                XMLHttpRequest.DONE ->
                    if (request.status.toInt() == 200) {
                        val response = kotlin.js.JSON.parse<RESULT>(request.responseText)
                        onSuccess(response)
                    } else {
                        onError("Status ${request.status}")
                    }
                XMLHttpRequest.UNSENT -> onError("Unsent")
            }
        }
        request.open("get", bypassCache(url), true)
        request.send()
    }

    private fun bypassCache(url: String): String {
        val now = Date().getTime()
        return if (url.contains("?")) {
            "$url&$now"
        } else {
            "$url?$now"
        }
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

