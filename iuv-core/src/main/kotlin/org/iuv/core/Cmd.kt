package org.iuv.core

import org.iuv.core.impl.MessageBusImpl

private class CmdNone<out MESSAGE> : Cmd<MESSAGE> {
    override fun run(messageBus: MessageBus<MESSAGE>) {
    }

    override fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Cmd<CONTAINER_MESSAGE> = Cmd.none()

}

abstract class Task<out RESULT,ERROR,MESSAGE> {
    private val errorH : SubListenersHelper<ERROR> = SubListenersHelper()
    private val successH : SubListenersHelper<RESULT> = SubListenersHelper()

    fun perform(onSuccess: (RESULT) -> MESSAGE, onFailure: (ERROR) -> MESSAGE) : Cmd<MESSAGE> {
        val toCmd = toCmd(successH, onSuccess, errorH, onFailure)
        start({ successH.dispatch(it) }, { errorH.dispatch(it) })
        return toCmd
    }

    protected abstract fun start(onSuccess: (RESULT) -> Unit, onError: (ERROR) -> Unit)

    fun <NEW_RESULT> andThen(continuation: (RESULT) -> Task<NEW_RESULT,ERROR,MESSAGE>) : Task<NEW_RESULT,ERROR,MESSAGE> {
        val self = this
        return object : Task<NEW_RESULT,ERROR,MESSAGE>() {
            override fun start(onSuccess: (NEW_RESULT) -> Unit, onError: (ERROR) -> Unit) {
                self.start( { t -> val task = continuation(t)
                    task.start(onSuccess, onError)
                }, onError)
            }
        }
    }

}

private fun <RESULT,ERROR,MESSAGE> toCmd(successH : SubListenersHelper<RESULT>, onSuccess : (RESULT) -> MESSAGE,
                                         failureH : SubListenersHelper<ERROR>, onFailure : (ERROR) -> MESSAGE) : Cmd<MESSAGE> {
    val subSuccess = successH.subscribe(onSuccess)
    val subFailure = failureH.subscribe(onFailure)

    return (object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            val listener: SubListener<MESSAGE> = object : SubListener<MESSAGE> {
                override fun onMessage(message: MESSAGE) {
                    subSuccess.removeListener(this)
                    subFailure.removeListener(this)
                    messageBus.send(message)
                }
            }
            subSuccess.addListener(listener)
            subFailure.addListener(listener)
        }
    })
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