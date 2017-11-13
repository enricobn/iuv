package org.iuv.core

import org.w3c.dom.events.Event
import kotlin.browser.window

typealias IUVRoute<MODEL, MESSAGE> = (List<String>) -> IUV<MODEL,MESSAGE>

// Model
data class RouterModel(val path : String, val currentIUVModel: Any?, val errorMessage: String?)

// Messages
interface RouterMessage

interface GotoMessage {
    val path: String
}

internal data class Goto(val path: String, val fromBrowser: Boolean) : RouterMessage

internal data class RouterMessageWrapper(val childMessage: Any) : RouterMessage

/**
 * @param testMode if true no window history events are captured nor sent.
 */
class IUVRouter(private val rootIUV: IUV<*,*>, val testMode : Boolean = false) : IUV<RouterModel, RouterMessage> {
    private var routes = HashMap<String, IUVRoute<*,*>>()
    private var errorMessage: String? = null
    private var baseUrl : String? = null

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, iuvRoute: IUVRoute<CHILD_MODEL, CHILD_MESSAGE>) {
        if (routes.containsKey(path)) {
            errorMessage = "Duplicate path: $path"
            return
        }
        routes[path] = iuvRoute
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, iuv: IUV<CHILD_MODEL, CHILD_MESSAGE>) =
            add(path, { iuv })

    override fun init() : Pair<RouterModel, Cmd<RouterMessage>> =
        init(window.location.href)


    /**
     * For test purposes
     *
     */
    internal fun init(href: String) : Pair<RouterModel, Cmd<RouterMessage>> {
        val (baseUrl, absolutePath) = parseHref(href)

        this.baseUrl = baseUrl

        val model = RouterModel("/", null, null)

        return Pair(model,
                Cmd.cmdOf(
                        sendMessage(Goto(absolutePath, true)),
                    object : Cmd<RouterMessage> {
                        override fun run(messageBus: MessageBus<RouterMessage>) {
                            if (!testMode) {
                                window.addEventListener("popstate", { _: Event ->
                                    val (_,poppedPath) = parseHref(window.location.href)
                                    messageBus.send(Goto(poppedPath, true))
                                })
                            }
                        }
                    }
                )
        )
    }

    override fun update(message: RouterMessage, model: RouterModel) : Pair<RouterModel, Cmd<RouterMessage>> =
        when (message) {
            is Goto -> {
                val absolutePath =
                        when {
                            message.path == "/" -> "/" // root
                            message.path.startsWith("/") -> message.path // absolute path
                            else -> model.path + message.path // relative path
                        }

                if (!testMode && !message.fromBrowser) {
                    if (absolutePath == "/") {
                        window.location.href = baseUrl!!
                    } else {
                        window.location.href = baseUrl + "#$absolutePath"
                    }
                }

                val (childIUV, error) = createChildIUV(absolutePath)

                if (childIUV == null) {
                    Pair(model.copy(errorMessage = error), Cmd.none())
                } else {
                    val (newModel, cmd) = childIUV.init(model)
                    Pair(newModel.copy(path = absolutePath, errorMessage = null), cmd)
                }

            }
            is RouterMessageWrapper -> {
                when (message.childMessage) {
                    is GotoMessage -> update(Goto(message.childMessage.path, false), model)
                    else -> {
                        val (childIUV, error) = createChildIUV(model)

                        if (childIUV != null) {
                            childIUV.update(message.childMessage, model)
                        } else {
                            Pair(model.copy(errorMessage = error), Cmd.none())
                        }
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
                val (childIUV, error) = createChildIUV(model)
                if (childIUV != null) {
                    childIUV.view(model, this)
                } else {
                    +error!!
                }
            }
        }

    /**
     * Returns a pair with baseURL and path
     */
    private fun parseHref(href: String) : Pair<String, String> {
        val hash = href.indexOf("#/")
        return if (hash >= 0)
            Pair(href.substring(0, hash), href.substring(hash + 1))
        else
            Pair(href, "/")
    }

    private fun createChildIUV(absolutePath: String): Pair<ChildIUV<RouterModel, RouterMessage, Any, Any>?,String?> {
        val childIUV: ChildIUV<RouterModel, RouterMessage, Any, Any>?

        if (absolutePath == "/") {
            childIUV = createChildIUV(rootIUV)
        } else {
            val baseUrl = routes.keys.sorted().reversed().find { absolutePath.startsWith("/" + it) }

            console.log(baseUrl)

            if (baseUrl != null) {
                val parameters = absolutePath.substring(baseUrl.length + 1)

                console.log(parameters)

                if (("/" + baseUrl) == absolutePath || parameters.startsWith("/")) {
                    try {
                        childIUV = createChildIUV(baseUrl, parameters)
                    } catch (e: Exception) {
                        return Pair(null, e.message)
                    }
                } else {
                    return Pair(null, "Cannot find path '$absolutePath'.")
                }
            } else {
                return Pair(null, "Cannot find path '$absolutePath'.")
            }
        }

        return Pair(childIUV, null)
    }

    private fun createChildIUV(model: RouterModel): Pair<ChildIUV<RouterModel, RouterMessage, Any, Any>?, String?> =
            if (model.path == "/")
                Pair(createChildIUV(rootIUV), null)
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