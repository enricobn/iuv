package org.iuv.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import org.iuv.shared.Task
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Http {

    fun <RESULT> GET(url: String, serializer: KSerializer<RESULT>, body: dynamic = null, async: Boolean = true,
                     username: String? = null,password: String? = null)  : Task<String,RESULT> where RESULT : Any =
        Task { onFailure, onSuccess ->
            request("get", url, serializer, onFailure, onSuccess, body, async, username, password)
        }

    fun <RESULT> PUT(url: String, serializer: KSerializer<RESULT>, body: dynamic = null, async: Boolean = true,
                     username: String? = null, password: String? = null)  : Task<String,RESULT> where RESULT : Any =
        Task { onFailure, onSuccess ->
            request("put", url, serializer, onFailure, onSuccess, body, async, username, password)
        }

    fun <RESULT> POST(url: String, serializer: KSerializer<RESULT>, body: dynamic = null, async: Boolean = true,
                      username: String? = null, password: String? = null)  : Task<String,RESULT> where RESULT : Any =
        Task { onFailure, onSuccess ->
            request("post", url, serializer, onFailure, onSuccess, body, async, username, password)
        }

    fun <RESULT> DELETE(url: String, serializer: KSerializer<RESULT>, body: dynamic = null, async: Boolean = true,
                        username: String? = null, password: String? = null) : Task<String,RESULT> where RESULT : Any =
        Task { onFailure, onSuccess ->
            request("delete", url, serializer, onFailure, onSuccess, body, async, username, password)
        }

    fun <RESULT> request(method: String,
                         url: String,
                         serializer: KSerializer<RESULT>,
                         onFailure: (String) -> Unit,
                         onSuccess: (RESULT) -> Unit,
                         body: dynamic,
                         async: Boolean = true,
                         username: String?,
                         password: String?
    ) where RESULT : Any {
        val request = XMLHttpRequest()

        if (body != null)
            request.send(body)

        request.onreadystatechange = { _ ->
            when (request.readyState) {
                XMLHttpRequest.DONE ->
                    if (request.status.toInt() == 200) {
                        val response = JSON.parse(serializer, request.responseText)
                        onSuccess(response)
                    } else {
                        onFailure("Status ${request.status}")
                    }
                XMLHttpRequest.UNSENT -> onFailure("Unsent")
            }
        }
        request.open(method, bypassCache(url), async, username, password)
        request.send()
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