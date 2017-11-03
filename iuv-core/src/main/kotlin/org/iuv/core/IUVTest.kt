package org.iuv.core

open class IUVTest<MESSAGE> {

    fun html(init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
        val html = HTML<MESSAGE>("div")
        init(html)
        return html
    }

    fun test(html: HTML<MESSAGE>) : TestingHtml<MESSAGE> =
        TestingHtml(html)

}

class TestingHtml<MESSAGE>(val html: HTML<MESSAGE>) {
    private val messageBus: SimpleMessageBus<MESSAGE> = SimpleMessageBus()

    init {
        html.nullableMessageBus = messageBus
    }

    fun getMessages() = messageBus.getMessages()

}

private class SimpleMessageBus<MESSAGE> : MessageBus<MESSAGE> {
    private val messages = mutableListOf<MESSAGE>()

    override fun send(message: MESSAGE) {
        messages.add(message)
    }

    fun getMessages() = messages.toList()

}