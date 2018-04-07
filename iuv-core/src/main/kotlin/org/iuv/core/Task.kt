package org.iuv.core

abstract class Task<out RESULT,ERROR> {

    fun <MESSAGE> perform(onSuccess: (RESULT) -> MESSAGE, onFailure: (ERROR) -> MESSAGE) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            start({ messageBus.send(onSuccess(it)) }, { messageBus.send(onFailure(it)) })
        }
    }

    fun <MESSAGE> run(onSuccess: (RESULT) -> Cmd<MESSAGE>, onFailure: (ERROR) -> Cmd<MESSAGE>) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            start({ onSuccess(it).run(messageBus) }, { onFailure(it).run(messageBus) })
        }
    }

    protected abstract fun start(onSuccess: (RESULT) -> Unit, onFailure: (ERROR) -> Unit)

    fun <NEW_RESULT> andThen(continuation: (RESULT) -> Task<NEW_RESULT, ERROR>) : Task<NEW_RESULT, ERROR> {
        val self = this
        return object : Task<NEW_RESULT, ERROR>() {
            override fun start(onSuccess: (NEW_RESULT) -> Unit, onFailure: (ERROR) -> Unit) {
                self.start( { t -> val task = continuation(t)
                    task.start(onSuccess, onFailure)
                }, onFailure)
            }
        }
    }

}