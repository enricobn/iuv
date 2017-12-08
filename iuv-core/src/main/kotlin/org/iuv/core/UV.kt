package org.iuv.core

import org.iuv.core.impl.MessageBusImpl
import org.w3c.xhr.XMLHttpRequest

private class CmdNone<out MESSAGE> : Cmd<MESSAGE> {
    override fun run(messageBus: MessageBus<MESSAGE>) {
    }

    override fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Cmd<CONTAINER_MESSAGE> = Cmd.none()

}

interface SubListener<in MESSAGE> {

    fun onMessage(message: MESSAGE)

}

private class SubNone<MESSAGE> : Sub<MESSAGE> {
    override fun addListener(listener: SubListener<MESSAGE>) {
    }

    override fun removeListener(listener: SubListener<MESSAGE>) {
    }

    override fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Sub<CONTAINER_MESSAGE> = Sub.none()

}

interface Sub<MESSAGE> {

    companion object {
        private val none = SubNone<Any>()

        fun <MESSAGE> none() = none as Sub<MESSAGE>

        fun <MESSAGE> of(vararg subs: Sub<MESSAGE>) : Sub<MESSAGE> {
            val notNone = subs.filter { it !is SubNone }

            if (notNone.isEmpty()) {
                return Sub.none()
            }

            return object : Sub<MESSAGE> {
                override fun addListener(listener: SubListener<MESSAGE>) {
                    notNone.forEach { it.addListener(listener) }
                }

                override fun removeListener(listener: SubListener<MESSAGE>) {
                    notNone.forEach { it.removeListener(listener) }
                }
            }
        }
    }

    fun addListener(listener: SubListener<MESSAGE>)

    fun removeListener(listener: SubListener<MESSAGE>)

    fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Sub<CONTAINER_MESSAGE> {
        val self = this
        return object : Sub<CONTAINER_MESSAGE> {
            private var thisListener : SubListener<MESSAGE>? = null

            override fun addListener(listener: SubListener<CONTAINER_MESSAGE>) {
                thisListener = object : SubListener<MESSAGE> {
                    override fun onMessage(message: MESSAGE) {
                        listener.onMessage(map(message))
                    }

                }
                thisListener.let {
                    self.addListener(it!!)
                }
            }

            override fun removeListener(listener: SubListener<CONTAINER_MESSAGE>) {
                if (thisListener != null) {
                    thisListener.let {
                        self.removeListener(it!!)
                    }
                }
            }
        }
    }
}

interface Cmd<out MESSAGE> {

    companion object {
        fun <MESSAGE> cmdOf(runFunction: (MessageBus<MESSAGE>) -> Unit) : Cmd<MESSAGE> {
            return object : Cmd<MESSAGE> {
                override fun run(messageBus: MessageBus<MESSAGE>) {
                    runFunction(messageBus)
                }
            }
        }

        fun <MESSAGE> cmdOf(vararg cmds: Cmd<MESSAGE>) : Cmd<MESSAGE> {
            val notNone = cmds.filter { it !is CmdNone }

            if (notNone.isEmpty()) {
                return none()
            }

            return object : Cmd<MESSAGE> {
                override fun run(messageBus: MessageBus<MESSAGE>) {
                    notNone.forEach { it.run(messageBus) }
                }
            }
        }

        private val none = CmdNone<Any>()

        fun <MESSAGE> none() : Cmd<MESSAGE> = none as Cmd<MESSAGE>

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