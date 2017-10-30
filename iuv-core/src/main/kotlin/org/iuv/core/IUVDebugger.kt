package org.iuv.core

// Model
data class IUVDebuggerModel(val iuvModel: Any?, val messages: List<Any>)

// Messages
interface IUVDebuggerMessage

private data class IUVDebuggerChildMessage(val message: Any) : IUVDebuggerMessage

class IUVDebugger<IUV_MODEL,IUV_MESSAGE>(iuv: IUV<IUV_MODEL,IUV_MESSAGE>) : IUV<IUVDebuggerModel,IUVDebuggerMessage> {

    val childIUV = ChildIUV<IUVDebuggerModel,IUVDebuggerMessage, IUV_MODEL, IUV_MESSAGE>(
            iuv,
            { IUVDebuggerChildMessage(it!!) },
            { it.iuvModel as IUV_MODEL},
            { parentModel,childModel -> parentModel.copy(iuvModel = childModel)}
    )

    override fun init(): Pair<IUVDebuggerModel, Cmd<IUVDebuggerMessage>> =
        childIUV.init(IUVDebuggerModel(null, emptyList()))


    override fun update(message: IUVDebuggerMessage, model: IUVDebuggerModel): Pair<IUVDebuggerModel, Cmd<IUVDebuggerMessage>> =
        when(message) {
            is IUVDebuggerChildMessage -> {
                val (newModel, cmd) = childIUV.update(message.message, model)
                Pair(newModel.copy(messages = newModel.messages + message.message), cmd)
            }
            else ->
                Pair(model, Cmd.none())
        }

    override fun view(model: IUVDebuggerModel): HTML<IUVDebuggerMessage> =
        html {
            childIUV.view(model, this)

            div {
                classes = "IUVDebugger"
                for (message in model.messages.reversed().take(10)) {
                    button {
                        classes = "IUVDebuggerButton"
                        +(message.toString())
//                                onClick {
//                                    model = hmodel
//                                    updateDocument(false)
//                                }
                    }
                }
            }
        }

}