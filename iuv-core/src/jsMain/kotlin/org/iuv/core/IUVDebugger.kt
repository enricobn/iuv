package org.iuv.core

import org.iuv.core.html.enums.InputType

// Model
data class IUVDebuggerModel(val iuvModel: Any?, val messagesAndModels: List<Pair<Any,Any>>, val index : Int)

// Messages
interface IUVDebuggerMessage

private data class IUVDebuggerChildMessage(val message: Any) : IUVDebuggerMessage

private data class SetModel(val index: Int) : IUVDebuggerMessage

class IUVDebugger<MODEL, MESSAGE>(view: View<MODEL, MESSAGE>) : View<IUVDebuggerModel,IUVDebuggerMessage> {

    private val childView = ChildView<IUVDebuggerModel,IUVDebuggerMessage, MODEL, MESSAGE>(
            view,
            { IUVDebuggerChildMessage(it!!) },
            { it.iuvModel as MODEL},
            { parentModel,childModel -> parentModel.copy(iuvModel = childModel)}
    )

    override fun subscriptions(model: IUVDebuggerModel): Sub<IUVDebuggerMessage> =
        childView.subscriptions(model)

    override fun init(): Pair<IUVDebuggerModel, Cmd<IUVDebuggerMessage>> {
        val init = childView.init()
        return Pair(IUVDebuggerModel(init.first, emptyList(), 0), init.second)
    }

    override fun update(message: IUVDebuggerMessage, model: IUVDebuggerModel): Pair<IUVDebuggerModel, Cmd<IUVDebuggerMessage>> =
        when(message) {
            is IUVDebuggerChildMessage -> {
                val (newModel, cmd) = childView.update(message.message as  MESSAGE, model)
                val messagesAndModels = newModel.messagesAndModels + (message.message!! to newModel.iuvModel!!)
                Pair(newModel.copy(messagesAndModels = messagesAndModels, index = messagesAndModels.size - 1), cmd)
            }
            is SetModel ->
                Pair(model.copy(iuvModel = model.messagesAndModels[message.index].second, index = message.index), Cmd.none())
            else ->
                Pair(model, Cmd.none())
        }

    override fun view(model: IUVDebuggerModel): HTML<IUVDebuggerMessage> =
        html {
            childView.addTo(this, model)
            div {
                classes = "IUVDebugger"

                if (model.messagesAndModels.isNotEmpty()) {
                    +"History: "
                    input {
                        id = "IUVDebugger-slider"
                        type = InputType.range
                        min = "0"
                        max = (model.messagesAndModels.size - 1).toString()
                        value = model.index.toString()
                        oninput { _ , value ->
                            SetModel(value.toInt())
                        }
                    }
                    div {
                        +model.messagesAndModels[model.index].first.toString()
                    }
//                    div {
//                        +model.messagesAndModels[model.index].second.toString()
//                    }
                }
            }
        }

}