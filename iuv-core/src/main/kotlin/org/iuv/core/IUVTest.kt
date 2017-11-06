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
                console.log(html.toStringDeep())
                console.log(other.toStringDeep())
                console.log(" not same children size")
                return false
            }
            if (html.children.filterIndexed { i, child -> !same(child, other.children[i])}.isNotEmpty()) {
                console.log(html.toStringDeep())
                console.log(other.toStringDeep())
                console.log(" not same children")
                return false
            }
            return true
        }

        private fun sameData(html: HTML<*>, other: HTML<*>) : Boolean {
            if (html.attrs != other.attrs) {
                console.log(html.toStringDeep())
                console.log(other.toStringDeep())
                console.log(" not same attrs")
                return false
            }

            if (html.handlers.keys != other.handlers.keys) {
                console.log(html.toStringDeep())
                console.log(other.toStringDeep())
                console.log(" not same keys")
                return false
            }

            return true
        }

        private fun same(child: HTMLChild, other: HTMLChild): Boolean {
            if (child === other) return true

            when (child) {
                is HTMLTextChild -> {
                    val result = child == other
                    if (!result) {
                        console.log(child.toString())
                        console.log(other.toString())
                        console.log("not same text child")
                    }
                    return result
                }
                is HTML<*> -> when(other) {
                    is HTML<*> -> return child.same(other)
                    else -> {
                        console.log(child.toString())
                        console.log(other.toString())
                        console.log("not same type")
                        return false
                    }
                }
                else -> {
                    console.log(child.toString())
                    console.log(other.toString())
                    console.log("unknown type")

                    return false
                }
            }
        }

        fun HTML<*>.same(other: HTML<*>): Boolean {
            if (this === other) return true

            if (name != other.name) {
                console.log(this.toStringDeep())
                console.log(other.toStringDeep())
                console.log("not same name")
                return false
            }
            if (text != other.text) {
                console.log(this.toStringDeep())
                console.log(other.toStringDeep())
                console.log("not same text")
                return false
            }
            if (!sameData(this, other)) {
                console.log(this.toStringDeep())
                console.log(other.toStringDeep())
                console.log("not same data")
                return false
            }
            if (!sameChildren(this, other)) {
                console.log(this.toStringDeep())
                console.log(other.toStringDeep())
                console.log("not same children")
                return false
            }

            return true
        }


//        fun <MESSAGE> HTML<MESSAGE>.same(other: HTML<MESSAGE>): Boolean {
//            if (this === other) return true
//            if (this::class.js != other::class.js) return false
//
//            if (name != other.name) return false
//            if (text != other.text) return false
//            if (!sameData(this, other)) return false
//            if (!sameChildren(this, other)) return false
//
//            return true
//        }

    }

    fun html(init: HTML<MESSAGE>.() -> Unit) : HTML<MESSAGE> {
        val html = HTML<MESSAGE>("div")
        init(html)
        return html
    }

    fun test(html: HTML<MESSAGE>) = TestingMainHTML(html)
}

open class TestingHTML(val html: HTML<*>, val parent: TestingHTML? = null) {

    fun find(predicate: HTMLPredicate) : TestingHTML? = find(null, html, predicate)

    companion object {

        private fun find(parent: TestingHTML?, html: HTML<*>, predicate: HTMLPredicate) : TestingHTML? =
            if (predicate(html as HTML<Any>)) {
                TestingHTML(html, parent)
            } else {
                val found = html.children.map { child ->
                    when (child) {
                        is HTML<*> -> {
                            find(TestingHTML(html, parent), child, predicate)
                        }
                        else -> null
                    }
                }.filter { it != null }

                if (found.size == 1) {
                    found.first()
                } else {
                    null
                }
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

class TestingMainHTML<MESSAGE>(html: HTML<MESSAGE>) : TestingHTML(html) {
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