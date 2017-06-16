package org.enricobn.iuv

import org.enricobn.iuv.impl.MessageBusImpl
import org.w3c.xhr.XMLHttpRequest

typealias Cmd<MESSAGE> = (MessageBus<MESSAGE>) -> Unit

fun <MESSAGE,CONTAINER_MESSAGE> mapCmd(cmd: Cmd<MESSAGE>?, map: (MESSAGE) -> CONTAINER_MESSAGE) : Cmd<CONTAINER_MESSAGE>? {
    if (cmd != null) {
        return { messageBus ->
            val newMessageBus = MessageBusImpl<MESSAGE>({ message -> messageBus.send(map(message)) })
            cmd(newMessageBus)
        }
    } else {
        return null
    }
}

interface UV<MODEL, MESSAGE> {

    fun update(message: MESSAGE, model: MODEL) : Pair<MODEL,Cmd<MESSAGE>?>

    fun view(model: MODEL): HTML<MESSAGE>.() -> Unit

    fun render(parent: HTML<MESSAGE>, model: MODEL) {
        view(model)(parent)
    }

    fun <J> getAsync(url: String, messageBus: MessageBus<MESSAGE>,
                     handler: (J) -> MESSAGE) {
        val request = XMLHttpRequest()
        request.onreadystatechange = { _ ->
            if (request.readyState.toInt() == 4 && request.status.toInt() == 200) {
                val response = kotlin.js.JSON.parse<J>(request.responseText)
                messageBus.send(handler(response))
            }
        }
        request.open("get", url, true)
        request.send()
    }

}