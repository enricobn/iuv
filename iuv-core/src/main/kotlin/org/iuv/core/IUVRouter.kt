package org.iuv.core

import org.w3c.dom.events.Event
import kotlin.browser.window

typealias IUVRoute<MODEL, MESSAGE> = (List<String>) -> IUV<MODEL,MESSAGE>

// Model
data class RouterModel(val currentIUV : ChildIUV<RouterModel, RouterMessage, *, *>?, val currentIUVModel: Any?,
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
        Pair(RouterModel(null, null, null), object : Cmd<RouterMessage> {
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
                    Pair(model.copy(currentIUV = null, errorMessage = "Cannot find URL ${message.url}."), Cmd.none<RouterMessage>())
                } else {
                    val parameters = message.url.substring(baseUrl.length)

                    console.log(parameters)

                    if (baseUrl != message.url && !parameters.startsWith("/")) {
                       Pair(model.copy(currentIUV = null, errorMessage = "Cannot find URL ${message.url}."), Cmd.none())
                    } else {
                        val route = routes[baseUrl]

                        try {
                            val iuv = route!!.invoke(parameters.substring(1).split("/").toList())

                            val childIUV = ChildIUV<RouterModel, RouterMessage, Any, Any>(
                                    iuv as IUV<Any, Any>,
                                    { RouterMessageWrapper(it) },
                                    { it.currentIUVModel!! },
                                    { parentModel, childModel -> parentModel.copy(currentIUVModel = childModel) }
                            )

                            val (newModel, cmd) = childIUV.init(model)
                            Pair(newModel.copy(currentIUV = childIUV, errorMessage = null), cmd)
                        } catch (e: Exception) {
                            Pair(model.copy(currentIUV = null, errorMessage = e.message), Cmd.none<RouterMessage>())
                        }
                    }
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