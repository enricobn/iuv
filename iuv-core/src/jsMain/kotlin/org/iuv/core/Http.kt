package org.iuv.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.UnitSerializer
import kotlinx.serialization.json.JSON
import org.iuv.shared.Task
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Http {

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> GET(url: String, serializer: KSerializer<RESULT>, body: BODY,
                          bodySerializer: KSerializer<BODY>, async: Boolean = true,
                          username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String, RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("get", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> GET(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String, RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("get", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> PUT(url: String, serializer: KSerializer<RESULT>, body: BODY,
                     bodySerializer: KSerializer<BODY>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("put", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> PUT(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("put", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> POST(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                      username: String? = null, password: String? = null, formData: Map<String, String>?,
                      queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("post", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, formData = formData, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> POST(url: String, serializer: KSerializer<RESULT>, body: BODY,
                      bodySerializer: KSerializer<BODY>, async: Boolean = true,
                      username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("post", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> DELETE(url: String, serializer: KSerializer<RESULT>, body: BODY,
                        bodySerializer: KSerializer<BODY>, async: Boolean = true,
                        username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("delete", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, setOf(200, 204), queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> DELETE(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                        username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("delete", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, setOf(200, 204), queryParams = queryParams)
            }

    fun <RESULT> request(method: String,
                         url: String,
                         serializer: KSerializer<RESULT>,
                         onFailure: (String) -> Unit,
                         onSuccess: (RESULT) -> Unit,
                         body: dynamic,
                         bodySerializer: KSerializer<Any>?,
                         async: Boolean = true,
                         username: String? = null,
                         password: String? = null,
                         successStatuses : Set<Int> = setOf(200),
                         formData: Map<String,String>? = null,
                         queryParams: Map<String,Any> = emptyMap()
    ) where RESULT : Any {
        val request = XMLHttpRequest()

        request.onreadystatechange = { _ ->
            when (request.readyState) {
                XMLHttpRequest.DONE ->
                    try {
                        if (successStatuses.contains(request.status.toInt())) {
                            if (serializer == UnitSerializer && request.responseText.isEmpty()) {
                                onSuccess(Unit as RESULT)
                            } else {
                                val response = JSON.parse(serializer, request.responseText)
                                onSuccess(response)
                            }
                        } else {
                            onFailure("Status ${request.status}")
                        }
                    } catch (e: Exception) {
                        onFailure(e.message ?: "Unknown error")
                        console.error(e)
                    }
                XMLHttpRequest.UNSENT -> onFailure("Unsent")
            }
        }

        val urlWithQueryParameters : String = urlWithQueryParameters(url, queryParams)

        request.open(method, bypassCache(urlWithQueryParameters), async, username, password)
        if (body != null) {
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
            request.send(JSON.stringify(bodySerializer!!, body))
        } else if (formData != null) {
            request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            request.send(formData.map { it.key + "=" + it.value }.joinToString("&"))
        } else
            request.send()
    }

    private fun urlWithQueryParameters(url: String, queryParams: Map<String, Any>): String {
        val queryParamsToString = queryParams.map { it.key + "=" + encodeQueryParam(it.value) }.joinToString("&")
        return if (url.contains("?")) {
            "$url&$queryParamsToString"
        } else {
            "$url?$queryParamsToString"
        }
    }

    private fun encodeQueryParam(value: Any): String {
        if (value is List<*>) {
            return value.joinToString(",") { encodeQueryParam(it ?: "") }
        }
        return value.toString()
    }

}

private fun bypassCache(url: String): String {
    val now = Date().getTime()
    return if (url.contains("?")) {
        "$url&$now"
    } else {
        "$url?$now"
    }
}

enum class HttpMethod(val method: String) {
    Get("get"),
    Put("put"),
    Post("post"),
    Delete("delete")
}

class HttpRequestRunner<RESULT: Any> private constructor(private val url: String, private val serializer: KSerializer<RESULT>,
                                                         private val method: HttpMethod) {
    private var body: dynamic = null
    private var bodySerializer: KSerializer<Any>? = null
    private var formData: Map<String,String>? = null
    private var queryParams: Map<String,Any> = emptyMap()
    private var async = true
    private var user: String? = null
    private var password: String? = null
    private var successStatuses : Set<Int> = emptySet()

    companion object {
        fun <RESULT: Any> runner(method: HttpMethod, url: String, serializer: KSerializer<RESULT>): HttpRequestRunner<RESULT> {
            val builder = HttpRequestRunner(url, serializer, method)
            if (method == HttpMethod.Delete) {
                builder.successStatuses = setOf(200, 204)
            } else {
                builder.successStatuses = setOf(200)
            }
            return builder
        }
    }

    fun <BODY : Any> body(body: BODY, bodySerializer: KSerializer<BODY>) : HttpRequestRunner<RESULT> {
        this.body = body
        this.bodySerializer = bodySerializer as KSerializer<Any>
        return this
    }

    fun formData(formData: Map<String,String>) : HttpRequestRunner<RESULT> {
        this.formData = formData
        return this
    }

    fun queryParams(queryParams: Map<String,Any>) : HttpRequestRunner<RESULT> {
        this.queryParams = queryParams
        return this
    }

    fun async(value: Boolean) {
        this.async = value
    }

    fun authenticate(user: String, password: String) : HttpRequestRunner<RESULT> {
        this.user = user
        this.password = password
        return this
    }

    fun run() = build().run()

    private fun build() =
            HttpRequest(method, url, serializer, body, bodySerializer, async, user, password, successStatuses,
                    formData, queryParams)

}

class HttpRequest<RESULT : Any>(private val method: HttpMethod,
                                private val url: String,
                                private val serializer: KSerializer<RESULT>,
                                private val body: dynamic,
                                private val bodySerializer: KSerializer<Any>?,
                                private val async: Boolean,
                                private val user: String?,
                                private val password: String?,
                                private val successStatuses : Set<Int>,
                                private val formData: Map<String,String>?,
                                private val queryParams: Map<String,Any>) {

    fun run() : Task<String,RESULT> =
        Task { onFailure, onSuccess ->
            Http.request(method.method, url, serializer, onFailure, onSuccess, body, bodySerializer, async, user, password,
                    successStatuses, formData, queryParams)
        }

}