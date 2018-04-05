package org.iuv.core

import org.iuv.core.impl.MessageBusImpl

private class CmdNone<out MESSAGE> : Cmd<MESSAGE> {
    override fun run(messageBus: MessageBus<MESSAGE>) {
    }

    override fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Cmd<CONTAINER_MESSAGE> = Cmd.none()

}

abstract class Task<T,MESSAGE> {
    private val errorH : SubListenersHelper<Unit> = SubListenersHelper()
    private val successH : SubListenersHelper<T> = SubListenersHelper()

    fun execute(onSuccess: (T) -> MESSAGE, onFailure: () -> MESSAGE) : Cmd<MESSAGE> {
        execute(successH, errorH)
        return toCmd(successH, onSuccess, errorH, onFailure)
    }

    protected abstract fun execute(successH: Dispatcher<T>, errorH: Dispatcher<Unit>)

    fun <T1> andThen(continuation: (T) -> Task<T1,MESSAGE>) : Task<T1,MESSAGE> {
        val self = this
        return object : Task<T1,MESSAGE>() {
            override fun execute(successH: Dispatcher<T1>, errorH: Dispatcher<Unit>) {
                self.errorH.subscribe { errorH.dispatch(Unit) }
                self.successH.subscribe { t -> val task = continuation(t)
                    task.execute(successH, errorH)
                }
            }
        }
    }

}

private fun <T,MESSAGE> toCmd(successH : SubListenersHelper<T>, onSuccess : (T) -> MESSAGE, failureH : SubListenersHelper<Unit>, mapFailure : () -> MESSAGE) : Cmd<MESSAGE> {
    val subSuccess = successH.subscribe(onSuccess)
    val subFailure = failureH.subscribe { mapFailure() }

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