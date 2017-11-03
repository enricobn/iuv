package org.iuv.core

typealias HTMLPredicate = (HTML<Any>) -> Boolean

open class IUVTest<MESSAGE> {

    companion object {

        infix fun HTMLPredicate.or(other: HTMLPredicate): HTMLPredicate = { html ->
            this(html) || other(html)
        }

        infix fun HTMLPredicate.and(other: HTMLPredicate): HTMLPredicate = { html ->
            this(html) && other(html)
        }



//        fun or(pred1: HTMLPredicate, pred2: HTMLPredicate) : HTMLPredicate = { html ->
//            pred1(html) || pred2(html)
//        }
//
//        fun and(pred1: HTMLPredicate, pred2: HTMLPredicate) : HTMLPredicate = { html ->
//            pred1(html) && pred2(html)
//        }

        fun not(predicate: HTMLPredicate) : HTMLPredicate = { html ->
            !predicate(html)
        }

        fun <MESSAGE> same(actual: HTML<MESSAGE>, expected: HTML<MESSAGE>) : Boolean =
                actual.getElementData().same(expected.getElementData())

        fun withAttribute(name: String, value: dynamic) : (HTML<Any>) -> Boolean = { html ->
            if (html.attrs.containsKey(name)) {
                html.attrs[name] == value
            } else {
                false
            }
        }

        fun withAttribute(name: String) : HTMLPredicate = { html ->
            html.attrs.containsKey(name)
        }

        fun withName(name: String) : HTMLPredicate = { html ->
            html.name == name
        }
    }

    fun html(init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
        val html = HTML<MESSAGE>("div")
        init(html)
        return html
    }

    fun test(html: HTML<MESSAGE>) = TestingMainHTML(html)
}

open class TestingHTML<MESSAGE>(val html: HTML<MESSAGE>) {

    fun find(predicate: HTMLPredicate) : TestingHTML<Any>? =
        if (predicate(html as HTML<Any>)) {
            this as TestingHTML<Any>
        } else {
            val found = html.children.filter {
                when(it) {
                    is HTMLElementChild -> predicate(it.html)
                    else -> false
                }
            }
            if (found.size == 1) {
                TestingHTML((found.first() as HTMLElementChild).html)
            } else {
                null
            }
        }

    fun callHandler(name: String, event: Any?) {
        html.handlers[name](event)
    }

    fun hasProperty(name: String) = html.attrs.containsKey(name)

    fun hasHandler(name: String) = html.handlers.containsKey(name)

    fun getProperty(name: String) = html.attrs[name]

    override fun toString(): String {
        return html.toString()
    }
}

class TestingMainHTML<MESSAGE>(html: HTML<MESSAGE>) : TestingHTML<MESSAGE>(html) {
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