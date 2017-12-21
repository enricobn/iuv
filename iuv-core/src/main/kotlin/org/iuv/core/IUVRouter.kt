package org.iuv.core

import org.w3c.dom.events.Event
import kotlin.browser.window

typealias IUVRoute<MODEL, MESSAGE> = (List<String>) -> IUV<MODEL,MESSAGE>

// Model
data class RouterModel(val path : String, val currentIUVModel: Any?, val errorMessage: String?)

// Messages
interface RouterMessage

data class Goto(val path: String, val fromBrowser: Boolean) : RouterMessage

internal data class RouterMessageWrapper(val childMessage: Any) : RouterMessage

/**
 * @param testMode if true no window history events are captured and the browser's location is not changed.
 */
class IUVRouter(private val rootIUV: IUV<*,*>, val testMode : Boolean = false) : IUV<RouterModel, RouterMessage> {
    private var routes = HashMap<String, IUVRoute<*,*>>()
    private var errorMessage: String? = null
    private var baseUrl : String? = null

    companion object {
        fun <MESSAGE> navigate(path: String) = object : Cmd<MESSAGE> {
            override fun run(messageBus: MessageBus<MESSAGE>) {
                if (path.startsWith("/")) {
                    window.location.hash = path
                } else {
                    window.location.hash = window.location.hash + "/" + path
                }

            }
        }
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

    override fun subscriptions(model: RouterModel): Sub<RouterMessage> {
        if (model.currentIUVModel != null) {
            val (childIUV, error) = createChildIUV(model)
            if (childIUV != null) {
                return childIUV.subscriptions(model)
            }
        }
        return Sub.none()
    }

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
                Cmd(sendMessage(Goto(absolutePath, true)),
                        object : Cmd<RouterMessage> {
                            override fun run(messageBus: MessageBus<RouterMessage>) {
                                if (!testMode) {
                                    window.addEventListener("popstate", { _: Event ->
                                        val (_, poppedPath) = parseHref(window.location.href)
                                        messageBus.send(Goto(poppedPath, true))
                                    })
                                }
                            }
                        }
                )
        )
    }

    override fun update(message: RouterMessage, model: RouterModel) : Pair<RouterModel, Cmd<RouterMessage>> {
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
                        window.location.href = baseUrl + "#"
                    } else {
                        window.location.href = baseUrl + "#$absolutePath"
                    }
                }

                val (childIUV, error) = createChildIUV(absolutePath)

                if (childIUV == null) {
                    return Pair(model.copy(errorMessage = error), Cmd.none())
                } else {
                    val (newModel, cmd) = childIUV.init(model)
                    return Pair(newModel.copy(path = absolutePath, errorMessage = null), cmd)
                }

            }
            is RouterMessageWrapper -> {
                val (childIUV, error) = createChildIUV(model)

                return if (childIUV != null) {
                    childIUV.update(message.childMessage, model)
                } else {
                    Pair(model.copy(errorMessage = error), Cmd.none())
                }
            }
            else -> {
                return Pair(model, Cmd.none())
            }
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

            if (baseUrl != null) {
                val parameters = absolutePath.substring(baseUrl.length + 1)

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