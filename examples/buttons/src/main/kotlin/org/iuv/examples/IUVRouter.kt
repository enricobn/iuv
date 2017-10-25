package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV
import org.iuv.core.MessageBus
import org.w3c.dom.events.Event
import kotlin.browser.window

// Model
data class RouterModel(val currentIUV : ChildIUV<RouterModel,RouterMessage,*,*>?, val currentIUVModel: Any?,
                       val errorMessage: String?)

// Messages
interface RouterMessage

interface GotoMessage {
    val url: String
}

private data class Goto(val url: String) : RouterMessage

private data class RouterMessageWrapper(val childMessage: Any) : RouterMessage

class IUVRouter : IUV<RouterModel, RouterMessage> {
    private var routes = HashMap<String, ChildIUV<RouterModel,RouterMessage,*,*>>()

    override fun init() : Pair<RouterModel, Cmd<RouterMessage>> {
        return Pair(RouterModel(null, null, null), object : Cmd<RouterMessage> {
            override fun run(messageBus: MessageBus<RouterMessage>) {
                messageBus.send(Goto("/"))
                window.addEventListener("popstate", { _: Event ->
                    console.log("popstate " + window.location)
                    messageBus.send(Goto(window.location.pathname))
                })
            }
        })
    }

    fun add(path: String, iuv: ChildIUV<RouterModel,RouterMessage,*,*>) {
        routes[path] = iuv
    }

    fun <CHILD_MODEL,CHILD_MESSAGE>add(path: String, iuv: IUV<CHILD_MODEL,CHILD_MESSAGE>) {
        routes[path] = ChildIUV<RouterModel,RouterMessage,CHILD_MODEL,CHILD_MESSAGE>(
                iuv,
                { RouterMessageWrapper(it as Any) },
                { it.currentIUVModel as CHILD_MODEL },
                { parentModel, childModel -> parentModel.copy(currentIUVModel = childModel) })
    }

    override fun update(message: RouterMessage, model: RouterModel) : Pair<RouterModel, Cmd<RouterMessage>> =
        when (message) {
            is Goto -> {
                window.history.pushState(object {}, "", message.url)

                console.log("IUVRouter " + message)
                val childIUV = routes[message.url]

                if (childIUV == null) {
                    Pair(model.copy(currentIUV = null, errorMessage = "Cannot find URL ${message.url}."), Cmd.none())
                } else {
                    val (newModel, cmd) = childIUV.init(model)
                    Pair(newModel.copy(currentIUV = childIUV, errorMessage = null), cmd)
                }
            }
            is RouterMessageWrapper -> {
                when (message.childMessage) {
                    is GotoMessage -> update(Goto(message.childMessage.url), model)
                    else -> model.currentIUV!!.update(message.childMessage, model)
                }
            }
            else -> {
                Pair(model, Cmd.none())
            }
        }

    override fun view(model: RouterModel): HTML<RouterMessage> =
        html {
            if (model.errorMessage != null) {
                +model.errorMessage
            } else {
                model.currentIUV!!.view(model, this)
            }
        }

}