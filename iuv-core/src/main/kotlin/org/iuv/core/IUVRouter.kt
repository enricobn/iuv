package org.iuv.core

import org.w3c.dom.events.Event
import kotlin.browser.window

typealias IUVRoute<MODEL, MESSAGE> = (List<String>) -> IUV<MODEL,MESSAGE>

// Model
data class RouterModel(val currentBaseURL : String?, val currentParameters: String?, val currentIUVModel: Any?,
                       val errorMessage: String?)

// Messages
interface RouterMessage

interface GotoMessage {
    val url: String
}

internal data class Goto(val url: String) : RouterMessage

internal data class RouterMessageWrapper(val childMessage: Any) : RouterMessage

/**
 * @param testMode if true no window history events are captured nor sent.
 */
class IUVRouter(rootIUV: IUV<*,*>, val testMode : Boolean = false) : IUV<RouterModel, RouterMessage> {
    private var routes = HashMap<String, IUVRoute<*, *>>()
    private var errorMessage: String? = null

    init {
        add("/", rootIUV)
    }

    override fun init() : Pair<RouterModel, Cmd<RouterMessage>> =
        Pair(RouterModel(null, null, null, null), object : Cmd<RouterMessage> {
            override fun run(messageBus: MessageBus<RouterMessage>) {
                messageBus.send(Goto("/"))

                if (!testMode) {
                    window.addEventListener("popstate", { _: Event ->
                        messageBus.send(Goto(window.location.pathname))
                    })
                }
            }
        })

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, iuvRoute: IUVRoute<CHILD_MODEL, CHILD_MESSAGE>) {
        if (routes.containsKey(path)) {
            errorMessage = "Duplicate path: $path"
            return
        }
        routes[path] = iuvRoute
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, iuv: IUV<CHILD_MODEL, CHILD_MESSAGE>) =
        add(path, { iuv })

    override fun update(message: RouterMessage, model: RouterModel) : Pair<RouterModel, Cmd<RouterMessage>> =
        when (message) {
            is Goto -> {
                if (!testMode) {
                    window.history.pushState(object {}, "", message.url)
                }

                val baseUrl = routes.keys.sorted().reversed().find { message.url.startsWith(it) }

                if (baseUrl == null) {
                    Pair(model.copy(errorMessage = "Cannot find URL ${message.url}."), Cmd.none())
                } else {
                    val parameters = message.url.substring(baseUrl.length)

                    if (baseUrl != message.url && !parameters.startsWith("/")) {
                       Pair(model.copy(errorMessage = "Cannot find URL ${message.url}."), Cmd.none())
                    } else {
                        try {
                            val childIUV = createChildIUV(baseUrl, parameters)
                            val (newModel, cmd) = childIUV.init(model)
                            Pair(newModel.copy(currentBaseURL = baseUrl, currentParameters = parameters, errorMessage = null), cmd)
                        } catch (e: Exception) {
                            Pair(model.copy(errorMessage = e.message), Cmd.none<RouterMessage>())
                        }
                    }
                }
            }
            is RouterMessageWrapper -> {
                when (message.childMessage) {
                    is GotoMessage -> update(Goto(message.childMessage.url), model)
                    else -> {
                        val childIUV = createChildIUV(model)

                        childIUV.update(message.childMessage, model)
                    }
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
                val childIUV = createChildIUV(model)
                childIUV.view(model, this)
            }
        }

    private fun createChildIUV(model: RouterModel): ChildIUV<RouterModel, RouterMessage, Any, Any> =
            createChildIUV(model.currentBaseURL!!, model.currentParameters!!)

    private fun createChildIUV(baseURL: String, parameters: String): ChildIUV<RouterModel, RouterMessage, Any, Any> {
        val route = routes[baseURL]

        val iuv = route!!.invoke(parameters.substring(1).split("/").toList())

        return ChildIUV(
                iuv as IUV<Any, Any>,
                { RouterMessageWrapper(it) },
                { it.currentIUVModel!! },
                { parentModel, childModel -> parentModel.copy(currentIUVModel = childModel) }
        )
    }

}