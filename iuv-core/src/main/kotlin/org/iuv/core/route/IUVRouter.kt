package org.iuv.core.route

import org.iuv.core.*
import org.w3c.dom.events.Event
import kotlin.browser.window


interface IUVRoute<MODEL, MESSAGE> {

    fun create(parameters: List<String>) : IUV<MODEL,MESSAGE>

}

class IUVRouteVoid<MODEL, MESSAGE>(private val iuv: IUV<MODEL, MESSAGE>) : IUVRoute<MODEL,MESSAGE> {

    override fun create(parameters: List<String>): IUV<MODEL, MESSAGE> = iuv

}

// Model
data class RouterModel(val currentIUV : ChildIUV<RouterModel, RouterMessage, *, *>?, val currentIUVModel: Any?,
                       val errorMessage: String?)

// Messages
interface RouterMessage

interface GotoMessage {
    val url: String
}

private data class Goto(val url: String) : RouterMessage

private data class RouterMessageWrapper(val childMessage: Any) : RouterMessage

class IUVRouter(rootIUV: IUV<*,*>) : IUV<RouterModel, RouterMessage> {
    private var routes = HashMap<String, IUVRoute<*, *>>()
    private var errorMessage: String? = null

    init {
        add("/", rootIUV)
    }

    override fun init() : Pair<RouterModel, Cmd<RouterMessage>> {
        return Pair(RouterModel(null, null, null), object : Cmd<RouterMessage> {
            override fun run(messageBus: MessageBus<RouterMessage>) {
                messageBus.send(Goto("/"))
                window.addEventListener("popstate", { _: Event ->
                    messageBus.send(Goto(window.location.pathname))
                })
            }
        })
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, iuvRoute: IUVRoute<CHILD_MODEL, CHILD_MESSAGE>) {
        if (routes.containsKey(path)) {
            errorMessage = "Duplicate path: $path"
            return
        }
        routes[path] = iuvRoute
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, provider: ((List<String>) -> IUV<CHILD_MODEL, CHILD_MESSAGE>)) {
        if (routes.containsKey(path)) {
            errorMessage = "Duplicate path: $path"
            return
        }
        routes[path] = object : IUVRoute<CHILD_MODEL,CHILD_MESSAGE> {
            override fun create(parameters: List<String>): IUV<CHILD_MODEL, CHILD_MESSAGE> = provider.invoke(parameters)
        }
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, iuv: IUV<CHILD_MODEL, CHILD_MESSAGE>) {
        add(path, IUVRouteVoid(iuv))
//        add(path, ChildIUV<RouterModel, RouterMessage, CHILD_MODEL, CHILD_MESSAGE>(
//                iuv,
//                { RouterMessageWrapper(it as Any) },
//                { it.currentIUVModel as CHILD_MODEL },
//                { parentModel, childModel -> parentModel.copy(currentIUVModel = childModel) })
//        )
    }

    override fun update(message: RouterMessage, model: RouterModel) : Pair<RouterModel, Cmd<RouterMessage>> =
        when (message) {
            is Goto -> {
                window.history.pushState(object {}, "", message.url)

                val baseUrl = routes.keys.sorted().reversed().find { message.url.startsWith(it) }

                if (baseUrl == null) {
                    Pair(model.copy(currentIUV = null, errorMessage = "Cannot find URL ${message.url}."), Cmd.none<RouterMessage>())
                } else {
                    val route = routes[baseUrl]

                    val iuv = route!!.create(message.url.substring(baseUrl.length + 1).split("/").toList())

                    val childIUV = ChildIUV<RouterModel,RouterMessage, Any, Any>(
                        iuv as IUV<Any,Any>,
                        { RouterMessageWrapper(it) },
                        { it.currentIUVModel!! },
                        { parentModel, childModel -> parentModel.copy(currentIUVModel = childModel) }
                    )

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
            errorMessage?.let {
                +it
                return@html
            }

            if (model.errorMessage != null) {
                +model.errorMessage
            } else {
                model.currentIUV!!.view(model, this)
            }
        }

}