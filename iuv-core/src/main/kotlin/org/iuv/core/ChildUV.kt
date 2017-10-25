package org.iuv.core

class ChildIUV<PARENT_MODEL,PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>(
        val childIUV: IUV<CHILD_MODEL, CHILD_MESSAGE>,
        private val messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE,
        toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL,
        private val modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
) : ChildUV<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>(childIUV,messageMapFun,toChildModelFun,modelUpdateFun) {

    fun init(parentModel: PARENT_MODEL) : Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
        val (childModel,childCmd) = childIUV.init()
        return Pair(modelUpdateFun(parentModel,childModel), childCmd.map(messageMapFun))
    }

}

open class ChildUV<PARENT_MODEL,PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>(
        val childUV: UV<CHILD_MODEL, CHILD_MESSAGE>,
        private val messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE,
        private val toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL,
        private val modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
) {

    fun view(model: PARENT_MODEL, parentHtml: HTML<PARENT_MESSAGE>) {
        childUV.view(toChildModelFun(model)).map(parentHtml, messageMapFun)
    }

    fun update(message: Any, parentModel: PARENT_MODEL) : Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> =
        try {
            val childMessage = message as CHILD_MESSAGE
            val (newModel,newCmd) = childUV.update(childMessage, toChildModelFun(parentModel))
            Pair(modelUpdateFun(parentModel,newModel), newCmd.map(messageMapFun))
        } catch (e: Exception) {
            Pair(parentModel, Cmd.none())
        }
}