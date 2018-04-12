package org.iuv.core

abstract class Task<ERROR,out RESULT> {

    fun <MESSAGE> perform(onFailure: (ERROR) -> MESSAGE, onSuccess: (RESULT) -> MESSAGE) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            start({ messageBus.send(onFailure(it)) }, { messageBus.send(onSuccess(it)) })
        }
    }

    fun <MESSAGE> run(onFailure: (ERROR) -> Cmd<MESSAGE>, onSuccess: (RESULT) -> Cmd<MESSAGE>) = object : Cmd<MESSAGE> {
        override fun run(messageBus: MessageBus<MESSAGE>) {
            start({ onFailure(it).run(messageBus) }, { onSuccess(it).run(messageBus) })
        }
    }

    protected abstract fun start(onFailure: (ERROR) -> Unit, onSuccess: (RESULT) -> Unit)

    fun <NEW_RESULT> andThen(continuation: (RESULT) -> Task<ERROR,NEW_RESULT>) : Task<ERROR,NEW_RESULT> {
        return object : Task<ERROR,NEW_RESULT>() {
            override fun start(onFailure: (ERROR) -> Unit, onSuccess: (NEW_RESULT) -> Unit) {
                this@Task.start(onFailure, { t -> val task = continuation(t)
                    task.start(onFailure, onSuccess)
                })
            }
        }
    }

}