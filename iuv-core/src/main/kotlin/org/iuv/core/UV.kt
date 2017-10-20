package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.xhr.XMLHttpRequest

interface Cmd<out MESSAGE> {

    companion object {
        fun <MESSAGE> cmdOf(runFunction: (MessageBus<MESSAGE>) -> Unit) : Cmd<MESSAGE> {
            return object : Cmd<MESSAGE> {
                override fun run(messageBus: MessageBus<MESSAGE>) {
                    runFunction(messageBus)
                }

            }
        }

        fun <MESSAGE> cmdOf(vararg cmds: Cmd<MESSAGE>?) : Cmd<MESSAGE>? {
            val notNull = cmds.filter { cmd -> cmd != null }

            return if (notNull.isEmpty()) {
                null
            } else {
                object : Cmd<MESSAGE> {
                    override fun run(messageBus: MessageBus<MESSAGE>) {
                        notNull.forEach { it?.run(messageBus) }
                    }
                }
            }
        }
    }

    fun run(messageBus: MessageBus<MESSAGE>)

    fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE) : Cmd<CONTAINER_MESSAGE> {
        return cmdOf { messageBus ->
            val newMessageBus = MessageBusImpl<MESSAGE>({ message -> messageBus.send(map(message)) })
            run(newMessageBus)
        }
    }
}

class GetAsync<in J, out MESSAGE>(private val url: String, private val handler: (J) -> MESSAGE) : Cmd<MESSAGE> {

    override fun run(messageBus: MessageBus<MESSAGE>) {
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

interface UV<MODEL, MESSAGE> {

    fun update(message: MESSAGE, model: MODEL) : Pair<MODEL, Cmd<MESSAGE>?>

    fun view(model: MODEL): HTML<MESSAGE>

    fun html(init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
        val html = HTML<MESSAGE>("div")
        init(html)
        return html
    }

}