package org.iuv.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.UnitSerializer
import kotlinx.serialization.json.JSON
import org.iuv.shared.Task
import org.w3c.files.File
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
                         formData: Map<String,Any>? = null,
                         queryParams: Map<String,Any> = emptyMap(),
                         multiPartData: List<MultiPartData>? = null
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

        if (multiPartData != null) {
            request.setRequestHeader("Content-Type", "multipart/form-data; boundary=blob")
            request.send(encodeMultipartData(multiPartData, "blob"))
        } else if (body != null) {
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
            request.send(JSON.stringify(bodySerializer!!, body))
        } else if (formData != null) {
            request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            request.send(encodeParams(formData))
        } else
            request.send()
    }

    // from https://developer.mozilla.org/en-US/docs/Learn/HTML/Forms/Sending_forms_through_JavaScript
    private fun encodeMultipartData(values: List<MultiPartData>, boundary: String): dynamic {
        var data = ""

        values.forEach { multiPartData ->

            when (multiPartData) {
                is MultipartFileParameter -> {
                    val file = multiPartData.file.file

                    // Start a new part in our body's request
                    data += "--$boundary\r\n";

                    // Describe it as form data
                    data += "content-disposition: form-data; "
                    // Define the name of the form data
                    data += "name=\"" + multiPartData.name + "\"; "
                    // Provide the real name of the file
                    data += "filename=\"" + file.name + "\"\r\n"
                    // And the MIME type of the file
                    data += "Content-Type: " + file.type + "\r\n"

                    // There's a blank line between the metadata and the data
                    data += "\r\n"

                    // Append the binary data to our body's request
                    data += (multiPartData.file.binary + "\r\n") as? Any
                }
                is MultipartParameter -> {
                    // Text data is simpler
                    // Start a new part in our body's request
                    data += "--$boundary\r\n"

                    // Say it's form data, and name it
                    data += "content-disposition: form-data; name=\"" + multiPartData.name + "\"\r\n"
                    // There's a blank line between the metadata and the data
                    data += "\r\n"

                    // Append the text data to our body's request
                    data += encodeParam(multiPartData.value) + "\r\n"
                }
            }

        }

        // Once we are done, "close" the body's request
        data += "--$boundary--";

        return data
    }

    fun <RESULT: Any> runner(method: HttpMethod, url: String, serializer: KSerializer<RESULT>) =
        if (method == HttpMethod.Delete) {
            HttpRequestRunner(url, serializer, method, setOf(200, 204))
        } else {
            HttpRequestRunner(url, serializer, method)
        }

    private fun urlWithQueryParameters(url: String, queryParams: Map<String, Any>): String {
        val queryParamsToString = encodeParams(queryParams)
        return if (url.contains("?")) {
            "$url&$queryParamsToString"
        } else {
            "$url?$queryParamsToString"
        }
    }

    private fun encodeParams(params: Map<String, Any>) =
            params.map { it.key + "=" + encodeParam(it.value) }.joinToString("&")

    private fun encodeParam(value: Any): String {
        if (value is List<*>) {
            return value.joinToString(",") { encodeParam(it ?: "") }
        }
        return value.toString()
    }

}

sealed class MultiPartData

data class MultipartParameter(val name: String, val value: Any) : MultiPartData()

data class MultipartFileParameter(val name: String, val file: MultipartFile) : MultiPartData()

data class MultipartFile(val file: File, val binary: dynamic)

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

class HttpRequestRunner<RESULT: Any>(private val url: String, private val serializer: KSerializer<RESULT>,
                                     private val method: HttpMethod, private val successStatuses : Set<Int> = setOf(200)) {
    private var body: dynamic = null
    private var bodySerializer: KSerializer<Any>? = null
    private var formData: Map<String,Any>? = null
    private var queryParams: Map<String,Any> = emptyMap()
    private var async = true
    private var user: String? = null
    private var password: String? = null
    private var multiPartData: List<MultiPartData>? = null

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

    fun multiPartData(values: List<MultiPartData>) : HttpRequestRunner<RESULT> {
        this.multiPartData = values
        return this
    }

    fun run() = build().run()

    private fun build() =
            HttpRequest(method, url, serializer, body, bodySerializer, async, user, password, successStatuses,
                    formData, queryParams, multiPartData)

}

class HttpRequest<RESULT : Any>(private val method: HttpMethod,
                                private val url: String,
                                private val serializer: KSerializer<RESULT>,
                                private val body: dynamic,
                                private val bodySerializer: KSerializer<Any>?,
                                private val async: Boolean,
                                private val user: String?,
                                private val password: String?,
                                private val successStatuses: Set<Int>,
                                private val formData: Map<String, Any>?,
                                private val queryParams: Map<String, Any>,
                                private val multiPartData: List<MultiPartData>?) {

    fun run() : Task<String,RESULT> =
        Task { onFailure, onSuccess ->
            Http.request(method.method, url, serializer, onFailure, onSuccess, body, bodySerializer, async, user, password,
                    successStatuses, formData, queryParams, multiPartData)
        }

}