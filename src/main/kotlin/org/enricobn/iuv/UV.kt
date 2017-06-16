package org.enricobn.iuv

import org.enricobn.iuv.impl.MessageBusImpl
import org.w3c.xhr.XMLHttpRequest

interface Cmd<out MESSAGE> {

    companion object {
        fun <MESSAGE> of(runFunction: (MessageBus<MESSAGE>) -> Unit) : Cmd<MESSAGE> {
            return object : Cmd<MESSAGE> {
                override fun run(messageBus: MessageBus<MESSAGE>) {
                    runFunction(messageBus)
                }

            }
        }
    }

    fun run(messageBus: MessageBus<MESSAGE>)

    fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE) : Cmd<CONTAINER_MESSAGE> {
        return of { messageBus ->
            val newMessageBus = MessageBusImpl<MESSAGE>({ message -> messageBus.send(map(message)) })
            run(newMessageBus)
        }
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