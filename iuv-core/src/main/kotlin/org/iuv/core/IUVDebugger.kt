package org.iuv.core

// Model
data class IUVDebuggerModel(val iuvModel: Any?, val messagesAndModels: List<Pair<Any,Any>>, val index : Int)

// Messages
interface IUVDebuggerMessage

private data class IUVDebuggerChildMessage(val message: Any) : IUVDebuggerMessage

private data class SetIUVModel(val index: Int) : IUVDebuggerMessage

class IUVDebugger<IUV_MODEL,IUV_MESSAGE>(iuv: IUV<IUV_MODEL,IUV_MESSAGE>) : IUV<IUVDebuggerModel,IUVDebuggerMessage> {

    val childIUV = ChildIUV<IUVDebuggerModel,IUVDebuggerMessage, IUV_MODEL, IUV_MESSAGE>(
            iuv,
            { IUVDebuggerChildMessage(it!!) },
            { it.iuvModel as IUV_MODEL},
            { parentModel,childModel -> parentModel.copy(iuvModel = childModel)}
    )

    override fun init(): Pair<IUVDebuggerModel, Cmd<IUVDebuggerMessage>> =
        childIUV.init(IUVDebuggerModel(null, emptyList(), 0))


    override fun update(message: IUVDebuggerMessage, model: IUVDebuggerModel): Pair<IUVDebuggerModel, Cmd<IUVDebuggerMessage>> =
        when(message) {
            is IUVDebuggerChildMessage -> {
                val (newModel, cmd) = childIUV.update(message.message as IUV_MESSAGE, model)
                val messagesAndModels = newModel.messagesAndModels + Pair(message.message!!, newModel.iuvModel!!)
                Pair(newModel.copy(messagesAndModels = messagesAndModels, index = messagesAndModels.size - 1), cmd)
            }
            is SetIUVModel ->
                Pair(model.copy(iuvModel = model.messagesAndModels[message.index].second, index = message.index), Cmd.none())
            else ->
                Pair(model, Cmd.none())
        }

    override fun view(model: IUVDebuggerModel): HTML<IUVDebuggerMessage> =
        html {
            childIUV.view(model, this)

            div {
                classes = "IUVDebugger"

                if (!model.messagesAndModels.isEmpty()) {
                    +"History: "
                    input {
                        type = "range"
                        min = 0
                        max = model.messagesAndModels.size - 1
                        value = model.index.toString()
                        onInput { _ , value ->
                            SetIUVModel(value.toInt())
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