package org.iuv.spring

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.JSON
import org.iuv.shared.Task
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

interface IUVSerializer {
    val serializer: KSerializer<*>
}

annotation class RouteSerializer(val value : KClass<out IUVSerializer>)

annotation class WebSocketAsync

abstract class ServiceVOServlet : HttpServlet() {

    @Autowired
    private var iuvSheduler: IUVScheduler? = null

    companion object {
        private val LOG = LoggerFactory.getLogger(ServiceVOServlet::class.java)
    }

    val routes : List<Route> by lazy {
        val routes = mutableListOf<Route>()

        this::class.memberFunctions
            .filter { KVisibility.PUBLIC == it.visibility }
            .forEach { fn ->
                val javaMethod = fn.javaMethod

                if (javaMethod != null) {

                    val annotations = AnnotationUtils.getAnnotations(javaMethod)

                    annotations?.forEach { annotation ->

                        val requestMapping = AnnotationUtils.getAnnotation(annotation, RequestMapping::class.java)

                        if (requestMapping != null) {

                            val annotationAttributes =
                                    AnnotatedElementUtils.getMergedAnnotationAttributes(javaMethod,
                                        RequestMapping::class.java)

                            if (annotationAttributes != null) {

                                val value = annotationAttributes.getStringArray("value")

                                val paths =
                                    if (value.isEmpty())
                                        arrayOf("")
                                    else
                                        value

                                paths.forEach { expressionPath ->
                                    if (routes.any {it.pathExpression == expressionPath &&
                                            it.methods.toList().intersect(requestMapping.method.toList()).isNotEmpty()}) {
                                        throw Exception("Route for '$expressionPath' already added for methods " +
                                                "${requestMapping.method.toList()}: $fn.")
                                    }
                                    routes.add(Route(requestMapping.method, expressionPath, fn,
                                            javaMethod.annotations.any { it is WebSocketAsync }))
                                }
                            }
                        }
                    }
                }
            }
        routes
    }

    override fun doGet(request: HttpServletRequest?, response: HttpServletResponse?) {
        processRequest(request!!, response!!)
    }

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doGet(req, resp)
    }

    override fun doHead(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doGet(req, resp)
    }

    override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doGet(req, resp)
    }

    override fun doOptions(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doGet(req, resp)
    }

    override fun doTrace(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doGet(req, resp)
    }

    override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doGet(req, resp)
    }

    private fun processRequest(request: HttpServletRequest, response: HttpServletResponse) {

        if (request.pathInfo != null && request.pathInfo.endsWith("clientapi")) {
            createClientApiResponse(request, response)
            return
        }

        val routesByMethod = routes.filter { it.methods.contains(RequestMethod.valueOf(request.method)) }

        val route = routesByMethod.firstOrNull { it.exactMatch(request.pathInfo) }
                ?: routesByMethod.firstOrNull { it.matches(request.pathInfo) }

        if (route == null) {
            response.status = HttpServletResponse.SC_NOT_FOUND
        } else {
            val params = mutableMapOf<KParameter, Any?>()

            params[route.function.parameters[0]] = this

            params.putAll(route.getParametersValues(request))
            try {
                val kSerializer = route.routeSerializer.value.objectInstance!!.serializer

                if (route.asynch) {
                    val webSocketResponseId = iuvSheduler?.scheduleTask(kSerializer as KSerializer<Any>) {
                        route.function.callBy(params) as Task<String,Any>
                    }

                    response.contentType = "text/plain"
                    response.writer.print(webSocketResponseId)
                } else {
                    val result = route.function.callBy(params)

                    when (result) {
                        is Task<*, *> -> {
                            result.run({
                                response.contentType = "text/plain"
                                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                                response.writer.print(it.toString())
                            }) {
                                response.contentType = "application/json"
                                if (it != null) {
                                    response.writer.print(stringify(kSerializer, it))
                                }
                            }
                        }
                        else -> {
                            response.contentType = "application/json"
                            if (result != null)
                                response.writer.print(stringify(kSerializer, result))
                        }
                    }
                }
            } catch (e: Exception) {
                val message = "Error executing ${route.function}."
                LOG.error(message, e)
                response.contentType = "text/plain"
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                response.writer.print("$message $e")
            }

        }
    }

    private fun createClientApiResponse(request: HttpServletRequest, response: HttpServletResponse) {
        response.contentType = "text/plain"

        val sb = StringBuilder()

        ClientAPIWriter(this, request.servletPath)
                .write(sb)

        response.writer.print(sb)
    }

    private inline fun <reified T> stringify(serializer: KSerializer<*>, value: T) : String where T : Any =
        JSON.stringify(serializer as SerializationStrategy<T>, value)

}

class Route(val methods: Array<RequestMethod>, val pathExpression: String, val function: KFunction<*>, val asynch : Boolean) {
    private val requestParams = mutableMapOf<KParameter,RequestParam>()
    private val requestBodies = mutableMapOf<KParameter,Pair<RequestBody,RouteSerializer>>()
    val routeMatcher = SimpleRouteMatcher(pathExpression)
    val pathVariables = mutableMapOf<KParameter,PathVariable>()
    val routeSerializer : RouteSerializer

    init {
        function.parameters.drop(1).forEach {
            val requestParam = it.findAnnotation<RequestParam>()
            val pathVariable = it.findAnnotation<PathVariable>()
            val requestBody = it.findAnnotation<RequestBody>()
            val routeSerializer = it.findAnnotation<RouteSerializer>()

            when {
                requestParam != null -> requestParams[it] = requestParam
                pathVariable != null -> pathVariables[it] = pathVariable
                requestBody != null -> {
                    if (routeSerializer == null)
                        throw ServletException("Parameter ${it.name} is annotated with RequestBody but is not annotated with RouteSerializer, in function $function.")
                    else
                        requestBodies[it] = Pair(requestBody, routeSerializer)
                }
                else -> throw ServletException("Parameter ${it.name} is not annotated with RequestParam or PathVariable, in function $function.")
            }
        }
        val returnType = function.findAnnotation<RouteSerializer>()
        if (returnType == null)
            throw ServletException("Function ${function.name} is not annotated with ReturnType.")
        else
            this.routeSerializer = returnType
    }

    fun matches(absolutePath: String?) = routeMatcher.matches(absolutePath)

    fun exactMatch(absolutePath: String?) = routeMatcher.exactMatch(absolutePath)

    fun getParametersValues(request: HttpServletRequest): Map<KParameter,Any?> {
        val pathVariablesFromRequest = routeMatcher.pathVariables(request.pathInfo)

        return function.parameters.drop(1).map {
            val value = getParameterValueFromRequest(it, request, pathVariablesFromRequest)

            Pair(it, value )
        }.toMap()
    }

    private fun getParameterValueFromRequest(it: KParameter, request: HttpServletRequest, pathVariablesFromRequest: Map<String, String>) : Any? =
        when {
            requestParams.containsKey(it) -> parse(it, request.getParameter(requestParams[it]!!.value))
            pathVariables.containsKey(it) -> parse(it, pathVariablesFromRequest[pathVariables[it]!!.value]!!)
            requestBodies.containsKey(it) ->  {
                val (_, routeSerializer) = requestBodies[it]!!
                val serializer = routeSerializer.value.objectInstance!!.serializer
                val body = getBody(request)
                JSON.parse(serializer, body)
            }
            else -> throw ServletException("Parameter ${it.name} is not annotated with RequestParam or PathVariable.")
        }

    private fun parse(it: KParameter, parameterValueString: String): Any =
        when {
            it.type == Int::class.createType() -> parameterValueString.toInt()
            it.type == Long::class.createType() -> parameterValueString.toLong()
            it.type == Boolean::class.createType() -> parameterValueString.toBoolean()
            else -> parameterValueString
        }

}

private fun getBody(request: HttpServletRequest): String {
    val stringBuilder = StringBuilder()
    val inputStream = request.inputStream
    if (inputStream != null) {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        bufferedReader.use {
            val charBuffer = CharArray(128)
            while (true) {
                val bytesRead = bufferedReader.read(charBuffer)
                if (bytesRead <= 0)
                    break
                stringBuilder.append(charBuffer, 0, bytesRead)
            }
        }
    }
    return stringBuilder.toString()
}