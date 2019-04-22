package org.iuv.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.UnitSerializer
import kotlinx.serialization.json.JSON
import org.iuv.shared.Task
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Http {

    fun <RESULT,BODY> GET(url: String, serializer: KSerializer<RESULT>, body: BODY,
                          bodySerializer: KSerializer<BODY>, async: Boolean = true,
                          username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String, RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("get", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    fun <RESULT> GET(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String, RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("get", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, queryParams = queryParams)
            }

    fun <RESULT,BODY> PUT(url: String, serializer: KSerializer<RESULT>, body: BODY,
                     bodySerializer: KSerializer<BODY>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("put", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    fun <RESULT> PUT(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("put", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, queryParams = queryParams)
            }

    fun <RESULT> POST(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                      username: String? = null, password: String? = null, formData: Map<String, String>?,
                      queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("post", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, formData = formData, queryParams = queryParams)
            }

    fun <RESULT,BODY> POST(url: String, serializer: KSerializer<RESULT>, body: BODY,
                      bodySerializer: KSerializer<BODY>, async: Boolean = true,
                      username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("post", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    fun <RESULT,BODY> DELETE(url: String, serializer: KSerializer<RESULT>, body: BODY,
                        bodySerializer: KSerializer<BODY>, async: Boolean = true,
                        username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("delete", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, setOf(200, 204), queryParams = queryParams)
            }

    fun <RESULT> DELETE(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                        username: String? = null, password: String? = null, queryParams: Map<String, Any> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("delete", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, setOf(200, 204), queryParams = queryParams)
            }

    // TOD can I make body and bodySerializer typed?
    fun <RESULT> request(method: String,
                         url: String,
                         serializer: KSerializer<RESULT>,
                         onFailure: (String) -> Unit,
                         onSuccess: (RESULT) -> Unit,
                         body: dynamic,
                         bodySerializer: KSerializer<Any>?,
                         async: Boolean = true,
                         username: String?,
                         password: String?,
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