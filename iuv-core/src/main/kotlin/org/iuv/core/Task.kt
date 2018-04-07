package org.iuv.core

abstract class Task<out RESULT,ERROR,MESSAGE> {
    private val errorH : SubListenersHelper<ERROR> = SubListenersHelper()
    private val successH : SubListenersHelper<RESULT> = SubListenersHelper()

    fun perform(onSuccess: (RESULT) -> MESSAGE, onFailure: (ERROR) -> MESSAGE) : Cmd<MESSAGE> {
        val cmd = toCmd(successH, onSuccess, errorH, onFailure)
        start({ successH.dispatch(it) }, { errorH.dispatch(it) })
        return cmd
    }

    fun run(onSuccess: (RESULT) -> Cmd<MESSAGE>, onFailure: (ERROR) -> Cmd<MESSAGE>) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            val subSuccess = successH.subscribe(onSuccess)
            val subFailure = errorH.subscribe(onFailure)

            val listener = object : SubListener<Cmd<MESSAGE>> {
                override fun onMessage(message: Cmd<MESSAGE>) {
                    subSuccess.removeListener(this)
                    subFailure.removeListener(this)
                    message.run(messageBus)
                }
            }
            subSuccess.addListener(listener)
            subFailure.addListener(listener)

            start({ successH.dispatch(it) }, { errorH.dispatch(it) })
        }
    }

    protected abstract fun start(onSuccess: (RESULT) -> Unit, onFailure: (ERROR) -> Unit)

    fun <NEW_RESULT> andThen(continuation: (RESULT) -> Task<NEW_RESULT, ERROR, MESSAGE>) : Task<NEW_RESULT, ERROR, MESSAGE> {
        val self = this
        return object : Task<NEW_RESULT, ERROR, MESSAGE>() {
            override fun start(onSuccess: (NEW_RESULT) -> Unit, onFailure: (ERROR) -> Unit) {
                self.start( { t -> val task = continuation(t)
                    task.start(onSuccess, onFailure)
                }, onFailure)
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