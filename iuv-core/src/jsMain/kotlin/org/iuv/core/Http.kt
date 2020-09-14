package org.iuv.core

import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.iuv.shared.Task
import org.w3c.files.File
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Http {

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> GET(url: String, serializer: KSerializer<RESULT>, body: BODY,
                          bodySerializer: KSerializer<BODY>, async: Boolean = true,
                          username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String, RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("get", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> GET(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String, RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("get", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> PUT(url: String, serializer: KSerializer<RESULT>, body: BODY,
                     bodySerializer: KSerializer<BODY>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("put", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> PUT(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                     username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("put", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> POST(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                      username: String? = null, password: String? = null, formData: Map<String, Any?> = emptyMap(),
                      queryParams: Map<String, Any?> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("post", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, formData = formData, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> POST(url: String, serializer: KSerializer<RESULT>, body: BODY,
                      bodySerializer: KSerializer<BODY>, async: Boolean = true,
                      username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("post", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT,BODY> DELETE(url: String, serializer: KSerializer<RESULT>, body: BODY,
                        bodySerializer: KSerializer<BODY>, async: Boolean = true,
                        username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("delete", url, serializer, onFailure, onSuccess, body, bodySerializer as KSerializer<Any>,
                        async, username, password, setOf(200, 204), queryParams = queryParams)
            }

    @Deprecated("Use HttpRequestRunner")
    fun <RESULT> DELETE(url: String, serializer: KSerializer<RESULT>, async: Boolean = true,
                        username: String? = null, password: String? = null, queryParams: Map<String, Any?> = emptyMap())
        : Task<String,RESULT> where RESULT : Any =
            Task { onFailure, onSuccess ->
                request("delete", url, serializer, onFailure, onSuccess, null, null, async,
                        username, password, setOf(200, 204), queryParams = queryParams)
            }

    @ExperimentalSerializationApi
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
                         formData: Map<String,Any?>? = null,
                         queryParams: Map<String,Any?> = emptyMap(),
                         multiPartData: List<MultiPartData>? = null,
                         headers: Map<String,Any?> = emptyMap(),
                         json: Json = Json
    ) where RESULT : Any {
        try {
            val request = XMLHttpRequest()

            request.onreadystatechange = {
                when (request.readyState) {
                    XMLHttpRequest.DONE ->
                        try {
                            if (successStatuses.contains(request.status.toInt())) {
                                if (isUnitSerializer(serializer) && request.responseText.isEmpty()) {
                                    onSuccess(Unit as RESULT)
                                } else {
                                    val response = json.decodeFromString(serializer, request.responseText)
                                    onSuccess(response)
                                }
                            } else {
                                onFailure("Unsupported status ${request.status} (${request.statusText}) see console for details.")
                            }
                        } catch (e: Exception) {
                            onFailure(e.message ?: "Unknown error")
                            console.error(e)
                        }
                    XMLHttpRequest.UNSENT -> onFailure("Unsent")
                }
            }

            request.onerror = {
                onFailure("Unknown error")
            }

            val urlWithQueryParameters: String = urlWithQueryParameters(url, queryParams)
            //request.withCredentials = username != null

            request.open(method, bypassCache(urlWithQueryParameters), async, username, password)

            headers.filter { it.value != null }.forEach {
                request.setRequestHeader(it.key, encodeParam(it.value!!)) // is correct to encode? Or get only strings?
            }

            if (multiPartData != null) {
                request.setRequestHeader("Content-Type", "multipart/form-data; boundary=blob")
                request.send(encodeMultipartData(multiPartData, "blob"))
            } else if (body != null) {
                request.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
                request.send(json.encodeToString(bodySerializer!!, body))
            } else if (formData != null) {
                request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                request.send(encodeParams(formData))
            } else
                request.send()
        } catch (e: Exception) {
            onFailure("Error sending the request: " + e.message)
        }
    }

    fun runner(method: HttpMethod, url: String) = HttpRequestRunner(method, url)

}

private fun <RESULT> isUnitSerializer(serializer: KSerializer<RESULT>) where RESULT : Any =
        serializer.descriptor.serialName.endsWith("UnitSerializer")

// from https://developer.mozilla.org/en-US/docs/Learn/HTML/Forms/Sending_forms_through_JavaScript
private fun encodeMultipartData(values: List<MultiPartData>, boundary: String): dynamic {
    var data = ""

    values.forEach { multiPartData ->

        when (multiPartData) {
            is MultiPartFileParameter -> {
                if (multiPartData.file != null) {
                    val file = multiPartData.file.file

                    // Start a new part in our body's request
                    data += "--$boundary\r\n"

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
            }
            is MultiPartParameter -> {
                if (multiPartData.value != null) {
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

    }

    // Once we are done, "close" the body's request
    data += "--$boundary--"

    return data
}

private fun urlWithQueryParameters(url: String, queryParams: Map<String, Any?>): String {
    val queryParamsToString = encodeParams(queryParams)
    return if (url.contains("?")) {
        "$url&$queryParamsToString"
    } else {
        "$url?$queryParamsToString"
    }
}

private fun encodeParams(params: Map<String, Any?>) =
        params.filter { it.value != null }.map { it.key + "=" + encodeParam(it.value!!) }.joinToString("&")

private fun encodeParam(value: Any): String {
    if (value is List<*>) {
        return value.joinToString(",") { encodeParam(it ?: "") }
    }
    return value.toString()
}

sealed class MultiPartData {

    companion object {
        fun of(name: String, value: Any?) = MultiPartParameter(name, value)

        fun of(name: String, file: MultipartFile?) = MultiPartFileParameter(name, file)

    }
}

data class MultiPartParameter(val name: String, val value: Any?) : MultiPartData()

data class MultiPartFileParameter(val name: String, val file: MultipartFile?) : MultiPartData()

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

data class HttpError(val status: Int, val statusText: String?, val message: String?, val responseHeaders: Map<String,String>, val responseText: String?)

data class HttpResult<RESULT: Any?>(val status: Int, val result: RESULT, val responseHeaders: Map<String,String>)

class HttpRequestRunner(private val method: HttpMethod, private val url: String) {
    private var json: Json = Json
    private var body: dynamic = null
    private var bodySerializer: KSerializer<Any>? = null
    private var formData: Map<String,Any?>? = null
    private var queryParams: Map<String,Any?> = emptyMap()
    private var async = true
    private var user: String? = null
    private var password: String? = null
    private var multiPartData: List<MultiPartData>? = null
    private var headers: MutableMap<String, Any?> = mutableMapOf()

    fun <BODY : Any> body(body: BODY, bodySerializer: KSerializer<BODY>) : HttpRequestRunner {
        this.body = body
        this.bodySerializer = bodySerializer as KSerializer<Any>
        return this
    }

    fun formData(formData: Map<String,String?>) : HttpRequestRunner {
        this.formData = formData
        return this
    }

    fun formData(vararg formData: Pair<String,String?>) : HttpRequestRunner {
        this.formData = formData.toMap()
        return this
    }

    fun queryParams(queryParams: Map<String,Any?>) : HttpRequestRunner {
        this.queryParams = queryParams
        return this
    }

    fun queryParams(vararg queryParams: Pair<String,Any?>) : HttpRequestRunner {
        this.queryParams = queryParams.toMap()
        return this
    }

    fun async(value: Boolean) {
        this.async = value
    }

    fun httpAuthentication(user: String, password: String) : HttpRequestRunner {
        this.user = user
        this.password = password
        return this
    }

    fun multiPartData(values: List<MultiPartData>) : HttpRequestRunner {
        this.multiPartData = values
        return this
    }

    fun multiPartData(vararg values: MultiPartData) : HttpRequestRunner {
        this.multiPartData = values.toList()
        return this
    }

    fun headers(values: Map<String,Any?>) : HttpRequestRunner {
        this.headers.putAll(values)
        return this
    }

    fun headers(vararg values: Pair<String,Any?>) : HttpRequestRunner {
        this.headers.putAll(values)
        return this
    }

    fun header(key: String, value: Any?) : HttpRequestRunner {
        this.headers[key] = value
        return this
    }

    fun configuration(configuration: HttpRequestRunnerConfiguration) : HttpRequestRunner {
        configuration.configure(this)
        return this
    }

    fun json(json: Json) : HttpRequestRunner {
        this.json = json
        return this
    }

    fun <BASE_RESULT: Any, RESULT: BASE_RESULT?> run(resultSerializer: KSerializer<BASE_RESULT>) : Task<HttpError,HttpResult<RESULT>> {
        return HttpRequest(method, url, body, bodySerializer, async, user, password, formData, queryParams, multiPartData,
                headers, json).run<BASE_RESULT, RESULT>(resultSerializer)
    }

}

private fun getResponseHeaders(request: XMLHttpRequest) =
        request.getAllResponseHeaders().split("\r\n").mapNotNull {
            val i = it.indexOf(":")
            if (i <= 0 || (i + 2) > it.length)
                null
            else
                Pair(it.substring(0, i), it.substring(i + 2))
        }.toMap()

class HttpRequest(
        private val method: HttpMethod,
        private val url: String,
        private val body: dynamic,
        private val bodySerializer: KSerializer<Any>?,
        private val async: Boolean,
        private val user: String?,
        private val password: String?,
        private val formData: Map<String, Any?>?,
        private val queryParams: Map<String, Any?>,
        private val multiPartData: List<MultiPartData>?,
        private val headers: Map<String, Any?>,
        private val json: Json) {

    fun <BASE_RESULT: Any, RESULT : BASE_RESULT?> run(resultSerializer: KSerializer<BASE_RESULT>) : Task<HttpError,HttpResult<RESULT>> =
        Task { onFailure, onSuccess ->
            val request = XMLHttpRequest()
            try {

                request.onreadystatechange = { _ ->
                    val status = request.status.toInt()
                    val responseHeaders = getResponseHeaders(request)
                    when (request.readyState) {
                        XMLHttpRequest.DONE ->
                            try {
                                if (request.status in 200..299) {
                                    if (isUnitSerializer(resultSerializer)) {
                                        onSuccess(HttpResult(status, Unit as RESULT, responseHeaders))
                                    } else if (status == 204) {
                                        onSuccess(HttpResult(status, null as RESULT, responseHeaders))
                                    } else {
                                        val response = json.decodeFromString(resultSerializer, request.responseText)
                                        onSuccess(HttpResult(status, response as RESULT, responseHeaders))
                                    }
                                } else if (request.status in 300..399) {
                                    // For what I have understood 3xx means redirection and should be handled by the
                                    // XMLHttpRequest itself, so if I get here probably there's something wrong.
                                    // TODO if I want to know that a redirection has happened (not here, but in 2xx statuses)
                                    //      then I can check responseURL, if it's not the same as the url then a redirection
                                    //      happened
                                    onFailure(HttpError(status, request.statusText, null, responseHeaders,
                                            request.responseText))
                                } else {
                                    onFailure(HttpError(status, request.statusText, null, responseHeaders,
                                            request.responseText))
                                }
                            } catch (e: Exception) {
                                onFailure(HttpError(status, request.statusText,e.message ?: "Unknown error",
                                        responseHeaders, request.responseText))
                                console.error(e)
                            }
                        XMLHttpRequest.UNSENT -> onFailure(HttpError(0, null,"Unsent message",
                                responseHeaders, null))
                    }
                }

                request.onerror = { _ ->
                    val responseHeaders = getResponseHeaders(request)
                    onFailure(HttpError(request.status.toInt(), request.statusText,"Unknown error", responseHeaders, request.responseText))
                }

                val urlWithQueryParameters: String = urlWithQueryParameters(url, queryParams)
                //request.withCredentials = username != null

                request.open(method.method, bypassCache(urlWithQueryParameters), async, user, password)

                headers.filter { it.value != null }.forEach {
                    request.setRequestHeader(it.key, encodeParam(it.value!!)) // is correct to encode? Or get only strings?
                }

                if (multiPartData != null) {
                    request.setRequestHeader("Content-Type", "multipart/form-data; boundary=blob")
                    request.send(encodeMultipartData(multiPartData, "blob"))
                } else if (body != null && bodySerializer != null) {
                    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
                    request.send(json.encodeToString(bodySerializer, body))
                } else if (formData != null) {
                    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    request.send(encodeParams(formData))
                } else
                    request.send()
            } catch (e: Exception) {
                onFailure(HttpError(request.status.toInt(), request.statusText, "Error sending the request: " + e.message,
                        getResponseHeaders(request), request.responseText))
            }
        }

}

interface HttpRequestRunnerConfiguration {

    fun configure(runner: HttpRequestRunner)

}

interface Authentication : HttpRequestRunnerConfiguration

class BasicAuthentication(private val username: String, private val password: String) : Authentication {
    override fun configure(runner: HttpRequestRunner) {
        runner.header("Authorization", "Basic " + window.btoa("$username:$password"))
    }

}


