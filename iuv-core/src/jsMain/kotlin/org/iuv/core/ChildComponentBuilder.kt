package org.iuv.core

import kotlin.reflect.KClass

interface ChildComponentBuilderMapMessage<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE : Any> {

    fun childMessageToParent(mapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE) : ChildComponentBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>
}

interface ChildComponentBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE : Any> {
    fun parentModelToChild(mapFun: (PARENT_MODEL) -> CHILD_MODEL) : ChildComponentBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>
}

interface ChildComponentBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE : Any> {
    fun updateParentModel(updateFun: (PARENT_MODEL, CHILD_MODEL) -> PARENT_MODEL) : ChildComponentBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>
}

interface ChildComponentBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE : Any> {
    fun build() : ChildComponent<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>
    fun <T : CHILD_MESSAGE> on(messageType: KClass<T>, toCmd : (T,PARENT_MODEL) -> Cmd<PARENT_MESSAGE>) : ChildComponentBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>
}

class ChildComponentBuilder<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE:Any> private constructor(private val component: Component<CHILD_MODEL,CHILD_MESSAGE>) :
        ChildComponentBuilderMapMessage<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>,
        ChildComponentBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>,
        ChildComponentBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>,
        ChildComponentBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE> {

    companion object {
        fun <PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE:Any>of(component: Component<CHILD_MODEL,CHILD_MESSAGE>) :
                ChildComponentBuilderMapMessage<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> = ChildComponentBuilder(component)
    }

    private lateinit var childMessageToParent: (CHILD_MESSAGE) -> PARENT_MESSAGE
    private lateinit var parentModelToChild: (PARENT_MODEL) -> CHILD_MODEL
    private lateinit var updateParentModel: (PARENT_MODEL, CHILD_MODEL) -> PARENT_MODEL
    private val ons: MutableList<Pair<KClass<Any>, (Any,PARENT_MODEL) -> Cmd<PARENT_MESSAGE>>> = mutableListOf()

    override fun childMessageToParent(mapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE) : ChildComponentBuilderToChildModel<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> {
        this.childMessageToParent = mapFun
        return this
    }

    override fun parentModelToChild(mapFun: (PARENT_MODEL) -> CHILD_MODEL) : ChildComponentBuilderUpdate<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> {
        this.parentModelToChild = mapFun
        return this
    }

    override fun updateParentModel(updateFun: (PARENT_MODEL, CHILD_MODEL) -> PARENT_MODEL) : ChildComponentBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE> {
        this.updateParentModel = updateFun
        return this
    }

    override fun build() : ChildComponent<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE> =
        object : ChildComponent<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,CHILD_MESSAGE>(component, childMessageToParent,
                parentModelToChild, updateParentModel) {

        override fun update(message: CHILD_MESSAGE, parentModel: PARENT_MODEL) : Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
            val (model, cmdFromUpdate) = super.update(message, parentModel)
            var cmd = cmdFromUpdate

            ons.forEach { (clazz, fn) ->
               if (message::class.js == clazz.js) {
                   cmd = Cmd(cmd, fn(message, parentModel))
               }
            }
            return Pair(model, cmd)
        }
    }

    override fun <T : CHILD_MESSAGE> on(messageType: KClass<T>, toCmd : (T,PARENT_MODEL) -> Cmd<PARENT_MESSAGE>) : ChildComponentBuilderBuild<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE> {
        ons.add(Pair(messageType as KClass<Any>, toCmd as (Any, PARENT_MODEL) -> Cmd<PARENT_MESSAGE>))
        return this
    }

    private inline fun <reified T : CHILD_MESSAGE> on_(message: CHILD_MESSAGE, toCmd : (T) -> Cmd<PARENT_MESSAGE>) =
        when (message) {
            is T -> toCmd(message)
            else -> Cmd.none()
        }

}
