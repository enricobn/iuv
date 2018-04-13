package org.iuv.core

import kotlin.reflect.KClass


interface ChildUVBuilderMapMessage<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE : Any> {

    fun mapChildMessage(messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE) : ChildUVBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>
}

interface ChildUVBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE : Any> {
    fun mapParentModel(toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL) : ChildUVBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>
}

interface ChildUVBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE : Any> {
    fun update(modelUpdateFun: (PARENT_MODEL, CHILD_MODEL) -> PARENT_MODEL) : ChildUVBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>
}

interface ChildUVBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE : Any> {
    fun build() : ChildUV<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>
    fun <T : CHILD_MESSAGE> on(messageType: KClass<T>, toCmd : (T,PARENT_MODEL) -> Cmd<PARENT_MESSAGE>) : ChildUVBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>
}

class ChildUVBuilder<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE:Any> private constructor(private val uv: UV<CHILD_MODEL,CHILD_MESSAGE>) :
        ChildUVBuilderMapMessage<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>,
        ChildUVBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>,
        ChildUVBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>,
        ChildUVBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE> {

    companion object {
        fun <PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE:Any>of(uv: UV<CHILD_MODEL,CHILD_MESSAGE>) :
                ChildUVBuilderMapMessage<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> = ChildUVBuilder(uv)
    }


    private lateinit var messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE
    private lateinit var toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL
    private lateinit var modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
    private val ons: MutableList<Pair<KClass<Any>, (Any,PARENT_MODEL) -> Cmd<PARENT_MESSAGE>>> = mutableListOf()

    override fun mapChildMessage(messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE) : ChildUVBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> {
        this.messageMapFun = messageMapFun
        return this
    }

    override fun mapParentModel(toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL) : ChildUVBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> {
        this.toChildModelFun = toChildModelFun
        return this
    }

    override fun update(modelUpdateFun: (PARENT_MODEL, CHILD_MODEL) -> PARENT_MODEL) : ChildUVBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE> {
        this.modelUpdateFun = modelUpdateFun
        return this
    }

    override fun build() : ChildUV<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> =
        object : ChildUV<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>(uv, messageMapFun, toChildModelFun, modelUpdateFun) {

        override fun update(message: CHILD_MESSAGE, parentModel: PARENT_MODEL) : Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
            val (model, cmdFromUpdate) = super.update(message, parentModel)
            var cmd = cmdFromUpdate

            ons.forEach {
               if (message::class.js == it.first.js) {
                   cmd = Cmd(cmd, it.second.invoke(message, parentModel))
               }
            }
            return Pair(model, cmd)
        }
    }

    override fun <T : CHILD_MESSAGE> on(messageType: KClass<T>, toCmd : (T,PARENT_MODEL) -> Cmd<PARENT_MESSAGE>) : ChildUVBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE> {
        ons.add(Pair(messageType as KClass<Any>, toCmd as (Any, PARENT_MODEL) -> Cmd<PARENT_MESSAGE>))
        return this
    }

    private inline fun <reified T : CHILD_MESSAGE> on_(message: CHILD_MESSAGE, toCmd : (T) -> Cmd<PARENT_MESSAGE>) =
        when (message) {
            is T -> toCmd(message)
            else -> Cmd.none()
        }

}
