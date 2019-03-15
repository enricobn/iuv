package org.iuv.spring

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import org.iuv.shared.IUVWebSocketMessage
import org.iuv.shared.Task
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.socket.*
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import java.util.*
import javax.servlet.http.HttpSession

class IUVWebSocketHandlerImpl : WebSocketHandler, IUVScheduler {
    companion object {
        private val LOG = LoggerFactory.getLogger(IUVWebSocketHandlerImpl::class.java)
    }
    private val sessionsByHttpSessionId = mutableMapOf<String,WebSocketSession>()

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {

    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val httpSessionId = getHttpSessionId(session)
        sessionsByHttpSessionId.remove(httpSessionId!!)

        LOG.info("WebSocket connection closed for httpSessionId $httpSessionId")
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {

    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val httpSessionId = getHttpSessionId(session)
        sessionsByHttpSessionId[httpSessionId!!] = session

        LOG.info("WebSocket connection established for httpSessionId $httpSessionId")
    }

    override fun supportsPartialMessages(): Boolean = false

    fun sendToHttpSession(sessionId: String, message: String) {
        LOG.debug("Sending message $message for session $sessionId")
        val session = sessionsByHttpSessionId[sessionId]
        synchronized(session!!) {
            session.sendMessage(TextMessage(message))
        }
    }

    fun sendToAll(message: String) {
        sessionsByHttpSessionId.values.forEach {
            synchronized(it) {
                it.sendMessage(TextMessage(message))
            }
        }
    }

    private fun getHttpSessionId(session: WebSocketSession) =
            session.attributes[HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME] as String?

    override fun <T: Any> scheduleTask(kSerializer: KSerializer<T>, function: () -> Task<String, T>): String {
        val id = UUID.randomUUID().toString()
        val sessionId = currentHttpSession().id
        Thread {
            val result = function.invoke()
            result.run({
                LOG.error("Sending error $it to session $sessionId")
                val iuvWebSocketMessage = IUVWebSocketMessage(id, null, it)
                sendToHttpSession(sessionId, JSON.stringify(IUVWebSocketMessage.serializer(), iuvWebSocketMessage))
            }) {
                val iuvWebSocketMessage = IUVWebSocketMessage(id, JSON.stringify(kSerializer, it), null)
                sendToHttpSession(sessionId, JSON.stringify(IUVWebSocketMessage.serializer(), iuvWebSocketMessage))
            }
        }.start()
        return id
    }

}

interface IUVScheduler {

    /**
     * schedule a task to be run asynchronously.
     * @return an unique id of the response, it's an implementation detail to know how to get the effective result
     * given an id.
     */
    fun <T : Any> scheduleTask(kSerializer: KSerializer<T>, function: () -> Task<String, T>): String

}

private fun currentHttpSession(): HttpSession {
    val attr = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    return attr.request.getSession(true) // true == allow create
}

