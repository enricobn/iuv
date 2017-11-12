package org.iuv.core

import org.w3c.dom.events.Event
import kotlin.browser.window

typealias IUVRoute<MODEL, MESSAGE> = (List<String>) -> IUV<MODEL,MESSAGE>

// Model
data class RouterModel(val path : String?, val currentIUVModel: Any?, val errorMessage: String?)

// Messages
interface RouterMessage

interface GotoMessage {
    val path: String
}

internal data class Goto(val path: String?, val fromBrowser: Boolean) : RouterMessage

internal data class RouterMessageWrapper(val childMessage: Any) : RouterMessage

/**
 * @param testMode if true no window history events are captured nor sent.
 */
class IUVRouter(private val rootIUV: IUV<*,*>, val testMode : Boolean = false) : IUV<RouterModel, RouterMessage> {
    private var routes = HashMap<String, IUVRoute<*, *>>()
    private var errorMessage: String? = null
    private var baseUrl : String? = null

    init {
//        add("/", rootIUV)
    }

    override fun init() : Pair<RouterModel, Cmd<RouterMessage>> {
        val (baseUrl, path) = parseHref(window.location.href)
        this.baseUrl = baseUrl
        val childIUV = createChildIUV(path)

        if (childIUV == null) {
            return Pair(RouterModel(path, null, "Cannot find path '$path'."), Cmd.none())
        } else {
            val (newModel, cmd) = childIUV.init(RouterModel(path, null, null))
            return Pair(newModel,
                    Cmd.cmdOf(cmd,
                        object : Cmd<RouterMessage> {
                            override fun run(messageBus: MessageBus<RouterMessage>) {
    //                        messageBus.send(Goto(null, false))

                                if (!testMode) {
                                    window.addEventListener("popstate", { _: Event ->
                                        val (_,path) = parseHref(window.location.href)
                                        messageBus.send(Goto(path, true))
                                    })
                                }
                            }
                        }
                    )
            )

        }

    }

    private fun parseHref(href: String) : Pair<String, String?> {
        val hash = href.indexOf("#/")
        return if (hash >= 0)
            Pair(href.substring(0, hash), href.substring(hash + 2))
        else
            Pair(href, null)
    }

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
                if (!testMode && !message.fromBrowser) {
                    if (message.path == null) {
                        window.location.href = baseUrl!!
//                        window.history.pushState(object {}, "", baseUrl)
                    } else {
                        window.location.href = baseUrl + "#/${message.path}"
//                        window.history.pushState(object {}, "", baseUrl + "#/${message.url}")
                    }
                }

                val childIUV: ChildIUV<RouterModel, RouterMessage, Any, Any>? =
                        createChildIUV(message.path)

                if (childIUV == null) {
                    Pair(model.copy(errorMessage = "Cannot find path '${message.path}'."), Cmd.none())
                } else {
                    val (newModel, cmd) = childIUV.init(model)
                    Pair(newModel.copy(path = message.path, errorMessage = null), cmd)
                }

            }
            is RouterMessageWrapper -> {
                when (message.childMessage) {
                    is GotoMessage -> update(Goto(message.childMessage.path, false), model)
                    else -> {
                        val childIUV = createChildIUV(model)

                        childIUV!!.update(message.childMessage, model)
                    }
                }
            }
            else -> {
                Pair(model, Cmd.none())
            }
        }

    private fun createChildIUV(path: String?): ChildIUV<RouterModel, RouterMessage, Any, Any>? {
        var childIUV: ChildIUV<RouterModel, RouterMessage, Any, Any>? = null

        if (path == null) {
            childIUV = createChildIUV(rootIUV)
        } else {
            val baseUrl = routes.keys.sorted().reversed().find { path.startsWith(it) }

            if (baseUrl != null) {
                val parameters = path.substring(baseUrl.length)

                if (baseUrl == path || parameters.startsWith("/")) {
                    try {
                        childIUV = createChildIUV(baseUrl, parameters)
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
            }
        }
        return childIUV
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
                childIUV!!.view(model, this)
            }
        }

    private fun createChildIUV(model: RouterModel): ChildIUV<RouterModel, RouterMessage, Any, Any>? =
            if (model.path == null)
                createChildIUV(rootIUV)
            else
                createChildIUV(model.path)

    private fun createChildIUV(baseURL: String, parameters: String): ChildIUV<RouterModel, RouterMessage, Any, Any> {
        val route = routes[baseURL]

        val iuv = route!!.invoke(parameters.substring(1).split("/").toList())

        return createChildIUV(iuv)
    }

    private fun createChildIUV(iuv: IUV<*, *>): ChildIUV<RouterModel, RouterMessage, Any, Any> {
        return ChildIUV(
                iuv as IUV<Any, Any>,
                { RouterMessageWrapper(it) },
                { it.currentIUVModel!! },
                { parentModel, childModel -> parentModel.copy(currentIUVModel = childModel) }
        )
    }

}