package org.iuv.core

import org.iuv.shared.Task
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Http {

    fun <RESULT> GET(url: String, async: Boolean, username: String? = null,
                              password: String? = null) = object : Task<String, RESULT> {

        override fun run(onFailure: (String) -> Unit, onSuccess: (RESULT) -> Unit) {
            val request = XMLHttpRequest()
            request.onreadystatechange = { _ ->
                when (request.readyState) {
                    XMLHttpRequest.DONE ->
                        if (request.status.toInt() == 200) {
                            val response = kotlin.js.JSON.parse<RESULT>(request.responseText)
                            onSuccess(response)
                        } else {
                            onFailure("Status ${request.status}")
                        }
                    XMLHttpRequest.UNSENT -> onFailure("Unsent")
                }
            }
            request.open("get", bypassCache(url), async, username, password)
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

