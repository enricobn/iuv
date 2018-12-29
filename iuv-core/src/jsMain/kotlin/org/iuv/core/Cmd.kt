package org.iuv.core

import org.iuv.core.impl.MessageBusImpl

fun <ERROR,RESULT,MESSAGE> Task<ERROR, RESULT>.toCmd(onFailure: (ERROR) -> MESSAGE, onSuccess: (RESULT) -> MESSAGE) =
    object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            run({ messageBus.send(onFailure(it)) }, { messageBus.send(onSuccess(it)) })
        }
    }

fun <ERROR,RESULT,MESSAGE> Task<ERROR, RESULT>.flatMapToCmd(onFailure: (ERROR) -> Cmd<MESSAGE>, onSuccess: (RESULT) -> Cmd<MESSAGE>) = object : Cmd<MESSAGE> {
    override fun run(messageBus: MessageBus<MESSAGE>) {
        run({ onFailure(it).run(messageBus) }, { onSuccess(it).run(messageBus) })
    }
}

private class CmdNone<out MESSAGE> : Cmd<MESSAGE> {
    override fun run(messageBus: MessageBus<MESSAGE>) {
    }

    override fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Cmd<CONTAINER_MESSAGE> = Cmd.none()

}

interface Cmd<out MESSAGE> {

    companion object {
        operator fun <MESSAGE> invoke(runFunction: (MessageBus<MESSAGE>) -> Unit) : Cmd<MESSAGE> {
            return object : Cmd<MESSAGE> {
                override fun run(messageBus: MessageBus<MESSAGE>) {
                    runFunction(messageBus)
                }
            }
        }

        operator fun <MESSAGE> invoke(vararg cmds: Cmd<MESSAGE>) : Cmd<MESSAGE> {
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

        operator fun <MESSAGE> invoke(cmds: List<Cmd<MESSAGE>>) : Cmd<MESSAGE> =
                invoke(*cmds.toTypedArray())

        operator fun <MESSAGE> invoke(message: MESSAGE) = object : Cmd<MESSAGE> {
            override fun run(messageBus: MessageBus<MESSAGE>) {
                messageBus.send(message)
            }
        }

        private val none = CmdNone<Any>()

        fun <MESSAGE> none() : Cmd<MESSAGE> = none as Cmd<MESSAGE>

    }

    fun run(messageBus: MessageBus<MESSAGE>)

    fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE) : Cmd<CONTAINER_MESSAGE> {
        return Cmd { messageBus ->
            val newMessageBus = MessageBusImpl<MESSAGE>({ message -> messageBus.send(map(message)) })
            run(newMessageBus)
        }
    }
}