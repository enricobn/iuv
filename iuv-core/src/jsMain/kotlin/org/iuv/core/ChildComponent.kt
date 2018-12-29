package org.iuv.core

class ChildView<PARENT_MODEL,PARENT_MESSAGE, CHILD_MODEL, in CHILD_MESSAGE>(
        private val childView: View<CHILD_MODEL, CHILD_MESSAGE>,
        private val messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE,
        toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL,
        private val modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
) : ChildComponent<PARENT_MODEL, PARENT_MESSAGE, CHILD_MODEL, CHILD_MESSAGE>(childView, messageMapFun, toChildModelFun, modelUpdateFun) {

    fun init() : Pair<CHILD_MODEL, Cmd<PARENT_MESSAGE>> {
        val (childModel,childCmd) = childView.init()
        return Pair(childModel, childCmd.map(messageMapFun))
    }

    fun initAndUpdate(message: CHILD_MESSAGE, parentModel: PARENT_MODEL): Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
        val (childModel, initCmd) = init()
        val (newParentModel, updateCmd) = update(message, modelUpdateFun.invoke(parentModel, childModel))
        return Pair(newParentModel, Cmd(initCmd, updateCmd))
    }

}

open class ChildComponent<PARENT_MODEL,PARENT_MESSAGE,CHILD_MODEL,in CHILD_MESSAGE>(
        private val childComponent: Component<CHILD_MODEL, CHILD_MESSAGE>,
        private val messageMapFun: (CHILD_MESSAGE) -> PARENT_MESSAGE,
        private val toChildModelFun: (PARENT_MODEL) -> CHILD_MODEL,
        private val modelUpdateFun: (PARENT_MODEL,CHILD_MODEL) -> PARENT_MODEL
) {

    fun subscriptions(model: PARENT_MODEL) : Sub<PARENT_MESSAGE> =
        Sub.map(childComponent.subscriptions(toChildModelFun(model)), messageMapFun)

    fun addTo(parentHtml: HTML<PARENT_MESSAGE>, model: PARENT_MODEL) {
        parentHtml.add(childComponent.view(toChildModelFun(model)), messageMapFun)
    }

    open fun update(message: CHILD_MESSAGE, parentModel: PARENT_MODEL) : Pair<PARENT_MODEL, Cmd<PARENT_MESSAGE>> {
        val (newModel, newCmd) = childComponent.update(message, toChildModelFun(parentModel))
        return Pair(modelUpdateFun(parentModel, newModel), newCmd.map(messageMapFun))
    }

}