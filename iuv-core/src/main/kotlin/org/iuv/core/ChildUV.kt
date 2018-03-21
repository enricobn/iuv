package org.iuv.core

class ChildIUV<PARENT_MODEL,PARENT_MESSAGE, CHILD_MODEL, in CHILD_MESSAGE>(
        private val childIUV: IUV<CHILD_MODEL, CHILD_MESSAGE>,
        private val messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE,
        toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL,
        private val modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
) : ChildUV<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>(childIUV,messageMapFun,toChildModelFun,modelUpdateFun) {

    fun init() : Pair<CHILD_MODEL, Cmd<PARENT_MESSAGE>> {
        val (childModel,childCmd) = childIUV.init()
        return Pair(childModel, childCmd.map(messageMapFun))
    }

    fun initAndUpdate(message: CHILD_MESSAGE, parentModel: PARENT_MODEL): Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
        val (childModel, initCmd) = init()
        val (newParentModel, updateCmd) = update(message, modelUpdateFun.invoke(parentModel, childModel))
        return Pair(newParentModel, Cmd(initCmd, updateCmd))
    }

}

open class ChildUV<PARENT_MODEL,PARENT_MESSAGE, CHILD_MODEL, in CHILD_MESSAGE>(
        private val childUV: UV<CHILD_MODEL, CHILD_MESSAGE>,
        private val messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE,
        private val toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL,
        private val modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
) {

    fun subscriptions(model: PARENT_MODEL) : Sub<PARENT_MESSAGE> =
        Sub.map(childUV.subscriptions(toChildModelFun(model)), messageMapFun)

    fun view(model: PARENT_MODEL, parentHtml: HTML<PARENT_MESSAGE>) {
        parentHtml.add(childUV.view(toChildModelFun(model)), messageMapFun)
    }

    fun update(message: CHILD_MESSAGE, parentModel: PARENT_MODEL) : Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
        val (newModel, newCmd) = childUV.update(message, toChildModelFun(parentModel))
        return Pair(modelUpdateFun(parentModel, newModel), newCmd.map(messageMapFun))
    }

}