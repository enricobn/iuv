package org.iuv.core

import org.iuv.core.IUVTest.Companion.same

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

//        fun <MESSAGE> same(actual: HTML<MESSAGE>, expected: HTML<MESSAGE>) : Boolean =
//                actual.getElementData().same(expected.getElementData())

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

        private fun sameChildren(html: HTML<*>, other: HTML<*>) : Boolean {
            if (other.children.size != html.children.size) {
                return false
            }
            if (html.children.filterIndexed { i, htmlData -> !same(htmlData, other.children[i])}.isNotEmpty()) {
                return false
            }
            return true
        }

        private fun sameData(html: HTML<*>, other: HTML<*>) : Boolean {
            if (html.attrs != other.attrs) {
                return false
            }

            if (html.handlers.keys != other.handlers.keys) {
                return false
            }

            return true
        }

        private fun same(child: HTMLChild, other: HTMLChild): Boolean {
            if (child === other) return true
            if (child::class.js != other::class.js) return false

            when (child) {
                is HTMLTextChild -> return child == other
                is HTML<*> -> when(other) {
                    is HTML<*> -> return child.same(other)
                    else -> return false
                }
                else -> return false
            }
        }

        fun HTML<*>.same(other: HTML<*>): Boolean {
            if (this === other) return true
            if (this::class.js != other::class.js) return false

//            when(this) {
//                is HTMLTextChild -> return this == other
//                is HTMLElementChild ->
//                    when(other) {
//                        is HTMLElementChild -> {
            if (name != other.name) return false
            if (text != other.text) return false
            if (!sameData(this, other)) return false
            if (!sameChildren(this, other)) return false
//                        }
//                        else -> return false
//                    }
//                else -> return false
//            }

            return true
        }


        fun <MESSAGE> HTML<MESSAGE>.same(other: HTML<MESSAGE>): Boolean {
            if (this === other) return true
            if (this::class.js != other::class.js) return false

//            when(this) {
//                is HTMLTextChild -> return this == other
//                is HTMLElementChild ->
//                    when(other) {
//                        is HTMLElementChild -> {
                            if (name != other.name) return false
                            if (text != other.text) return false
                            if (!sameData(this, other)) return false
                            if (!sameChildren(this, other)) return false
//                        }
//                        else -> return false
//                    }
//                else -> return false
//            }

            return true
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
            val found = html.children.filter { child ->
                when(child) {
                    is HTML<*> -> predicate(child as HTML<Any>)
                    else -> false
                }
            }
            if (found.size == 1) {
                TestingHTML(found.first() as HTML<Any>)
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