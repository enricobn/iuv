package org.iuv.shared

interface Task<ERROR,out RESULT> {

    companion object {
        operator fun <ERROR,RESULT> invoke(handler: ((ERROR) -> Unit, (RESULT) -> Unit) -> Unit) : Task<ERROR, RESULT> =
            SimpleTask(handler)
    }

    fun run(onFailure: (ERROR) -> Unit, onSuccess: (RESULT) -> Unit)

    fun <NEW_RESULT> andThen(continuation: (RESULT) -> Task<ERROR, NEW_RESULT>) : Task<ERROR, NEW_RESULT> {
        return object : Task<ERROR, NEW_RESULT> {
            override fun run(onFailure: (ERROR) -> Unit, onSuccess: (NEW_RESULT) -> Unit) {
                this@Task.run(onFailure, { t -> val task = continuation(t)
                    task.run(onFailure, onSuccess)
                })
            }
        }
    }

}

private class SimpleTask<ERROR,out RESULT>(private val handler: ((ERROR) -> Unit, (RESULT) -> Unit) -> Unit) : Task<ERROR, RESULT> {
    override fun run(onFailure: (ERROR) -> Unit, onSuccess: (RESULT) -> Unit) {
        handler(onFailure, onSuccess)
    }
}