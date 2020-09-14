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
                this@Task.run(onFailure, { t ->
                    val task = continuation(t)
                    task.run(onFailure, onSuccess)
                })
            }
        }
    }

    fun <NEW_ERROR, NEW_RESULT> map(mapError: (ERROR) -> NEW_ERROR, mapResult: (RESULT) -> NEW_RESULT) : Task<NEW_ERROR, NEW_RESULT> {
        return object : Task<NEW_ERROR, NEW_RESULT> {
            override fun run(onFailure: (NEW_ERROR) -> Unit, onSuccess: (NEW_RESULT) -> Unit) {
                this@Task.run({ error : ERROR -> onFailure.invoke(mapError.invoke(error))}, {
                    result: RESULT -> onSuccess.invoke(mapResult.invoke(result))
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