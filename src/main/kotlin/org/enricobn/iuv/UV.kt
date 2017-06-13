package org.enricobn.iuv

import org.w3c.xhr.XMLHttpRequest

interface UV<MODEL, MESSAGE, CONTAINER_MESSAGE> {

    fun update(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE, message: MESSAGE, model: MODEL) : Pair<MODEL,(() -> Unit)?>

    fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE, model: MODEL): HTML.() -> Unit

    fun render(parent: HTML, messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE, model: MODEL) {
        view(messageBus, map, model)(parent)
    }

    fun <J> getAsync(url: String, messageBus: MessageBus<CONTAINER_MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE,
                     handler: (J) -> MESSAGE) {
        val request = XMLHttpRequest()
        request.onreadystatechange = { _ ->
            if (request.readyState.toInt() == 4 && request.status.toInt() == 200) {
                val response = kotlin.js.JSON.parse<J>(request.responseText)
                messageBus.send(map(handler(response)))
            }
        }
        request.open("get", url, true)
        request.send()
    }

}