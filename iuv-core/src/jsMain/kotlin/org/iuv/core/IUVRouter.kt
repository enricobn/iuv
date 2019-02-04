package org.iuv.core

import org.w3c.dom.events.Event
import kotlin.browser.window

typealias IUVRoute<MODEL, MESSAGE, PARAMETERS> = (PARAMETERS) -> View<MODEL,MESSAGE>

interface RouteMatcher<PARAMETERS> {

    fun matches(absolutePath: String) : Boolean

    fun parameters(absolutePath: String) : Map<String,String>

    fun validate() : String?

    fun toParam(parameters : Map<String,String>) : PARAMETERS

    fun link(parameter: PARAMETERS) : String

    fun <MESSAGE> goto(parameter: PARAMETERS) = IUVRouter.navigate<MESSAGE>(link(parameter))

}

//class RegexRouteMatcher(private val regex: Regex) : RouteMatcher {
//
//    override fun matches(absolutePath: String) =
//        regex.matches(absolutePath)
//
//    override fun parameters(absolutePath: String): List<String> {
//        return regex.find(absolutePath)?.groupValues ?: emptyList()
//    }
//
//}

abstract class AbstractRouteMatcher<P>(expression: String) : RouteMatcher<P> {
    private val expComponents = expression.split("/")

    init {
        if (!expression.startsWith("/")) {
            throw Exception("Expression must start with /")
        }
    }

    final override fun matches(absolutePath: String) : Boolean {
        if (absolutePath.contains(":")) return false

        val pathComponents = absolutePath.split(("/"))

        if (pathComponents.size != expComponents.size) return false

        return expComponents.zip(pathComponents).all { it.first.startsWith(":") || it.first == it.second }
    }

    final override fun parameters(absolutePath: String): Map<String,String> {
        val pathComponents = absolutePath.split(("/"))

        return expComponents
                .zip(pathComponents)
                .filter { it.first.startsWith(":")  }
                .map { Pair(it.first.substring(1), it.second) }
                .toMap()
    }

    final override fun validate(): String? = null

}

class SimpleRouteMatcher(val path: String) : AbstractRouteMatcher<Unit>(path) {

    override fun link(parameter: Unit): String = path

    override fun toParam(parameters: Map<String, String>) = Unit

}

class StringParameterMatcher(val path: String) : AbstractRouteMatcher<String>("$path/:id") {

    override fun link(parameter: String): String = "$path/$parameter"

    override fun toParam(parameters: Map<String, String>): String = parameters["id"]!!

}

// Model
data class RouterModel(val path : String, val currentModel: Any?, val errorMessage: String?)

// Messages
interface RouterMessage

data class Goto(val path: String, val fromBrowser: Boolean) : RouterMessage

internal data class RouterMessageWrapper(val path: String, val childMessage: Any) : RouterMessage

typealias MatcherAndRoute<MODEL, MESSAGE, PARAMETERS> =
        Pair<RouteMatcher<PARAMETERS>, IUVRoute<MODEL, MESSAGE, PARAMETERS>>
/**
 * @param testMode if true no window history events are captured and the browser's location is not changed.
 */
class IUVRouter(private val rootView: View<*,*>, val testMode : Boolean = false) : View<RouterModel, RouterMessage> {
    private var routes = mutableListOf<MatcherAndRoute<*,*,*>>()
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

    fun <CHILD_MODEL,CHILD_MESSAGE,PARAMETERS> add(routeMatcher: RouteMatcher<PARAMETERS>, iuvRoute: IUVRoute<CHILD_MODEL, CHILD_MESSAGE, PARAMETERS>) {
        if (routes.any { routeMatcher == it.first }) {
            errorMessage = "Duplicate matcher: $routeMatcher."
            return
        }
        routes.add(Pair(routeMatcher, iuvRoute))
    }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(path: String, view: View<CHILD_MODEL, CHILD_MESSAGE>) =
            add(SimpleRouteMatcher(path)) { view }

    fun <CHILD_MODEL,CHILD_MESSAGE> add(routeMatcher: SimpleRouteMatcher, view: View<CHILD_MODEL, CHILD_MESSAGE>) =
            add(routeMatcher) { view }

    override fun subscriptions(model: RouterModel): Sub<RouterMessage> {
        if (model.currentModel != null) {
            val (childView, error) = createChildView(model)
            if (childView != null) {
                return childView.subscriptions(model)
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

                val (childView, error) = createChildView(absolutePath)

                if (childView == null) {
                    return Pair(model.copy(path = absolutePath, errorMessage = error), Cmd.none())
                } else {
                    val (childModel, cmd) = childView.init()
                    return Pair(model.copy(path = absolutePath, currentModel = childModel, errorMessage = null), cmd)
                }

            }
            is RouterMessageWrapper -> {
                if (message.path != model.path) {
                    console.info("IUVRouter, ignored message from another route: $message.")
                    return Pair(model, Cmd.none())
                }
                val (childView, error) = createChildView(model)

                return if (childView != null) {
                    childView.update(message.childMessage, model)
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
                val (childView, error) = createChildView(model)
                if (childView != null) {
                    childView.addTo(this, model)
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

    private fun createChildView(absolutePath: String): Pair<ChildView<RouterModel, RouterMessage, Any, Any>?,String?> {
        val childView: ChildView<RouterModel, RouterMessage, Any, Any>?

        if (absolutePath == "/") {
            childView = createChildView(absolutePath, rootView)
        } else {
            val route = routes.firstOrNull { it.first.matches(absolutePath) }

            if (route != null) {
                val parameters = route.first.parameters(absolutePath)

                try {
                    childView = createChildView(absolutePath, route as MatcherAndRoute<*, *, Any>, parameters)
                } catch (e: Exception) {
                    return Pair(null, e.message)
                }
            } else {
                return Pair(null, "Cannot find path '$absolutePath'.")
            }
        }

        return Pair(childView, null)
    }

    private fun createChildView(model: RouterModel): Pair<ChildView<RouterModel, RouterMessage, Any, Any>?, String?> =
            if (model.path == "/")
                Pair(createChildView(model.path, rootView), null)
            else
                createChildView(model.path)

    private fun createChildView(path: String, route: MatcherAndRoute<*,*,Any>, parameters: Map<String,String>): ChildView<RouterModel, RouterMessage, Any, Any> {

        val iuv = route.second.invoke(route.first.toParam(parameters))

        return createChildView(path, iuv)
    }

    private fun createChildView(path: String, view: View<*, *>): ChildView<RouterModel, RouterMessage, Any, Any> {
        return ChildView(
                view as View<Any, Any>,
                { RouterMessageWrapper(path, it) },
                { it.currentModel!! },
                { parentModel, childModel -> parentModel.copy(currentModel = childModel) }
        )
    }

}