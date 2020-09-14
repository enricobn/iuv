package org.iuv.spring

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.iuv.shared.Task
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Method
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType

interface IUVSerializer {
    val serializer: KSerializer<*>
}

annotation class RouteSerializer(val value : KClass<out IUVSerializer>)

annotation class WebSocketAsync

@ExperimentalSerializationApi
abstract class ServiceVOServlet : HttpServlet() {

    @Autowired
    private var iuvScheduler: IUVScheduler? = null

    companion object {
        private val LOG = LoggerFactory.getLogger(ServiceVOServlet::class.java)

        private data class ServletFunction(val fn : KFunction<*>) {

            fun requestMappingFunction(): RequestMappingFunction? {
                val javaMethod = fn.javaMethod
                return if (javaMethod == null) {
                    null
                } else {
                    val requestMapping = AnnotationUtils.findAnnotation(javaMethod, RequestMapping::class.java)
                    return if (requestMapping == null) {
                        null
                    } else {
                        RequestMappingFunction(fn, javaMethod, requestMapping)
                    }
                }
            }

        }

        private data class RequestMappingFunction(val fn : KFunction<*>, val javaMethod: Method, val requestMapping: RequestMapping) {

            fun attributes(): RequestMappingAttributes? {
                val annotationAttributes =
                        AnnotatedElementUtils.findMergedAnnotationAttributes(javaMethod,
                                RequestMapping::class.java, false, false)

                return if (annotationAttributes == null) {
                    null
                } else {
                    RequestMappingAttributes(fn, requestMapping, annotationAttributes)
                }
            }
        }

        private data class RequestMappingAttributes(val fn: KFunction<*>, val requestMapping: RequestMapping,
                                                    val annotationAttributes: AnnotationAttributes)

        fun getRoutes(kClass: KClass<out ServiceVOServlet>): List<Route> {
            val routes = mutableListOf<Route>()
            kClass.memberFunctions
                    .filter { KVisibility.PUBLIC == it.visibility}
                    .map { ServletFunction(it) }
                    .mapNotNull { it.requestMappingFunction() }
                    .mapNotNull { it.attributes() }
                    .forEach { (fn, requestMapping, annotationAttributes) ->
                        val value = annotationAttributes.getStringArray("value")

                        val paths =
                                if (value.isEmpty())
                                    arrayOf("")
                                else
                                    value

                        paths.forEach { expressionPath ->
                            if (routes.any {
                                        it.pathExpression == expressionPath &&
                                                it.methods.toList().intersect(requestMapping.method.toList()).isNotEmpty()
                                    }) {
                                throw Exception("Route for '$expressionPath' already added for methods " +
                                        "${requestMapping.method.toList()} in function ${fn.name}.")
                            }
                            routes.add(Route(requestMapping.method, expressionPath, fn))
                        }
                    }
            return routes.toList()
        }

    }

    val routes : List<Route> by lazy {
        getRoutes(this::class)
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
                    val webSocketResponseId = iuvScheduler?.scheduleTask(kSerializer as KSerializer<Any>) {
                        route.function.callBy(params) as Task<String,Any>
                    }

                    response.contentType = "text/plain"
                    response.writer.print(webSocketResponseId)
                } else {

                    when (val result = route.function.callBy(params)) {
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
        Json.encodeToString(serializer as SerializationStrategy<T>, value)

}

private fun <A : Annotation> findAnnotation(function: KFunction<*>, kParameter: KParameter,
                                                           annotationClass : KClass<A>) : A? {
    val javaMethod = function.javaMethod ?: return null

    val annotation = findMergedAnnotation(javaMethod, kParameter, annotationClass)

    if (annotation != null) {
        return annotation
    }

    val interfaceMethod = javaMethod.declaringClass.interfaces
            .flatMap { findSimilarFunction(function, it) }
            .firstOrNull()

    if (interfaceMethod != null) {
        return findMergedAnnotation(interfaceMethod, kParameter, annotationClass)
    }

    return null
}

private fun <A : Annotation> findMergedAnnotation(javaMethod: Method, kParameter: KParameter, annotationClass: KClass<A>): A? =
        AnnotatedElementUtils.findMergedAnnotation(javaMethod.parameters[kParameter.index - 1], annotationClass.java)

private fun findSimilarFunction(function: KFunction<*>, inClass: Class<*>): List<Method> {
    return inClass.methods.filter { method ->
        method.name == function.name &&
        method.parameterTypes.contentEquals(function.parameters.drop(1).map { parameter -> parameter.type.javaType }.toTypedArray())
    }
}

data class PathVariableParameter(val variableName : String, val parameterName : String, val required : Boolean)

class Route(val methods: Array<RequestMethod>, val pathExpression: String, val function: KFunction<*>) {
    private val requestParams = mutableMapOf<KParameter,RequestParam>()
    private val requestBodies = mutableMapOf<KParameter,Pair<RequestBody,RouteSerializer>>()
    private val pathVariables = mutableMapOf<KParameter,PathVariableParameter>()
    val routeMatcher = ServletRouteMatcher(pathExpression)
    val routeSerializer : RouteSerializer
    val asynch: Boolean
    val pathVariableParameters: List<PathVariableParameter>

    init {
        val javaMethod = function.javaMethod ?: throw ServletException("Function ${function.name} is not a javaMethod.")

        val pathVariableParameters = mutableListOf<PathVariableParameter>()

        function.parameters.drop(1).forEach {
            val requestParam = findAnnotation(function, it, RequestParam::class)
            val pathVariable = findAnnotation(function, it, PathVariable::class)
            val requestBody = findAnnotation(function, it, RequestBody::class)
            val routeSerializer = findAnnotation(function, it, RouteSerializer::class)

            when {
                requestParam != null -> requestParams[it] = requestParam
                pathVariable != null -> {
                    val pathVariableName = if (pathVariable.name == "") it.name!! else pathVariable.name

                    if (!routeMatcher.pathVariableNames.contains(pathVariableName)) {
                        throw ServletException("$pathVariableName does not match mapping for function ${function.name}.")
                    }

                    val pathVariableParameter = PathVariableParameter(pathVariableName, it.name!!, pathVariable.required)
                    pathVariables[it] = pathVariableParameter
                    pathVariableParameters.add(pathVariableParameter)
                }
                requestBody != null -> {
                    if (routeSerializer == null)
                        throw ServletException("Parameter ${it.name} is annotated with RequestBody but is not annotated with RouteSerializer, in function $function.")
                    else
                        requestBodies[it] = Pair(requestBody, routeSerializer)
                }
                else -> throw ServletException("Parameter ${it.name} is not annotated with RequestParam or PathVariable, in function $function.")
            }
        }

        this.pathVariableParameters = pathVariableParameters

        val routeSerializer = AnnotationUtils.findAnnotation(javaMethod, RouteSerializer::class.java)

        if (routeSerializer == null)
            throw ServletException("Function ${function.name} is not annotated with RouteSerializer.")
        else
            this.routeSerializer = routeSerializer

        this.asynch = AnnotationUtils.findAnnotation(javaMethod, WebSocketAsync::class.java) != null
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

    fun getRequestBodies() = requestBodies.toMap()

    fun getRequestParams() = requestParams.toMap()

    private fun getParameterValueFromRequest(it: KParameter, request: HttpServletRequest, pathVariablesFromRequest: Map<String, String>) : Any? =
        when {
            requestParams.containsKey(it) -> parse(it, request.getParameter(requestParams[it]!!.value))
            pathVariables.containsKey(it) -> parse(it, pathVariablesFromRequest.getValue(pathVariables[it]!!.variableName))
            requestBodies.containsKey(it) ->  {
                val (_, routeSerializer) = requestBodies[it]!!
                val serializer = routeSerializer.value.objectInstance!!.serializer
                val body = getBody(request)
                Json.decodeFromString(serializer, body)
            }
            else -> throw ServletException("Parameter ${it.name} is not annotated with RequestParam or PathVariable.")
        }

    private fun parse(it: KParameter, parameterValueString: String): Any =
            when (it.type) {
                Int::class.createType() -> parameterValueString.toInt()
                Long::class.createType() -> parameterValueString.toLong()
                Boolean::class.createType() -> parameterValueString.toBoolean()
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