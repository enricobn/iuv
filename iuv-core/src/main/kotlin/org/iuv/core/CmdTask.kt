package org.iuv.core

@Deprecated("Use Task")
interface CmdTask<out T,MESSAGE> {

    companion object {
        /**
         * Creates a CmdRunner given the SAM, since to implement CmdRunner you only need to define a single function,
         * you can pass it to this factory to create a CmdRunner.
         * SAM stands for "single abstract method" (or functional interface in java).
         */
        operator fun <T,MESSAGE> invoke(sam: ((Exception?) -> Cmd<MESSAGE>, onSuccess: (T) -> Cmd<MESSAGE>) -> Cmd<MESSAGE>) =
                object : CmdTask<T, MESSAGE> {
                    override fun run(onFailure: (Exception?) -> Cmd<MESSAGE>, onSuccess: (T) -> Cmd<MESSAGE>): Cmd<MESSAGE> =
                            sam(onFailure, onSuccess)
                }
    }

    fun <T1> andThen(cmdTask: (T) -> CmdTask<T1, MESSAGE>) : CmdTask<T1, MESSAGE> {
        val self = this
        return object : CmdTask<T1, MESSAGE> {

            override fun run(onFailure: (Exception?) -> Cmd<MESSAGE>, onSuccess: (T1) -> Cmd<MESSAGE>): Cmd<MESSAGE> =
                    self.run(onFailure) {
                        cmdTask(it).run(onFailure,onSuccess)
                    }
        }
    }

    fun run(onFailure: (Exception?) -> Cmd<MESSAGE> = { Cmd.none() }, onSuccess: (T) -> Cmd<MESSAGE>) : Cmd<MESSAGE>

}