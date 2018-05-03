package org.iuv.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import org.iuv.shared.Task
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

object Http {

    fun <RESULT> GET(url: String, async: Boolean, serializer: KSerializer<RESULT>, username: String? = null,
                                    password: String? = null)  : Task<String,RESULT> where RESULT : Any{

        return object : Task<String,RESULT> {
            override fun run(onFailure: (String) -> Unit, onSuccess: (RESULT) -> Unit) {
                val request = XMLHttpRequest()
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
                request.open("get", bypassCache(url), async, username, password)
                request.send()
            }

            private fun bypassCache(url: String): String {
                val now = Date().getTime()
                return if (url.contains("?")) {
                    "$url&$now"
                } else {
                    "$url?$now"
                }
            }

        }
    }

}