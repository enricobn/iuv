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

        fun not(predicate: HTMLPredicate) : HTMLPredicate = { html ->
            !predicate(html)
        }

        fun withAttribute(name: String, value: dynamic) : (HTML<Any>) -> Boolean = { html ->
            html.getAttribute(name) == value
        }

        fun withAttribute(name: String) : HTMLPredicate = { html ->
            html.getAttribute(name) != null
        }

        fun withName(name: String) : HTMLPredicate = { html ->
            html.name == name
        }

        private fun sameChildren(html: HTML<*>, other: HTML<*>) : SameResult {
            if (other.getChildren().size != html.getChildren().size) {
                return SameResult("Not same children size.")
            }

            val childrenSameResults = html.getChildren().mapIndexed { i, child -> same(child, other.getChildren()[i]) }

            val errors = childrenSameResults.filter { !it.same }

            if (errors.isNotEmpty()) {
                return errors.first()
            }
            return SameResult()
        }

        private fun sameData(html: HTML<*>, other: HTML<*>) : SameResult {
            if (html.getAttrs() != other.getAttrs()) {
                return SameResult("Not same attributes, '${html.getAttrs()}' vs '${other.getAttrs()}'.")
            }

            if (html.getHandlers().keys != other.getHandlers().keys) {
                return SameResult("Not same name handlers (keys), '${html.getHandlers().keys}' vs '${other.getHandlers().keys}'.")
            }

            return SameResult()
        }

        private fun same(child: HTMLChild, other: HTMLChild): SameResult {
            if (child === other) return SameResult()

            when (child) {
                is HTMLTextChild -> when (other) {
                    is HTMLTextChild -> {
                        val result = child == other
                        if (!result) {
                            return SameResult("Not same text child, '${child.text}' vs '${other.text}'.")
                        }
                        return SameResult()
                    } else -> return SameResult("Not same child type.")
                }
                is HTML<*> -> when(other) {
                    is HTML<*> -> return child.same(other)
                    else -> return SameResult("Not same child type.")
                }
                else -> {
                    return SameResult("Unknown child type.")
                }
            }
        }


        fun assertSameHTML(expected: HTML<*>, actual: HTML<*>) {
            val sameResult = expected.same(actual)

            if (!sameResult.same) {
                console.log(sameResult.message)
                console.log("Expected:")
                console.log(expected.toStringDeep())
                console.log("Actual:")
                console.log(actual.toStringDeep())
                throw AssertionError(sameResult.message + " Look at the console for more details.")
            }
        }

        fun HTML<*>.same(other: HTML<*>): SameResult {
            if (this === other) return SameResult()

            if (name != other.name) {
                return SameResult("Not same name, '$name' vs '${other.name}'.")
            }

            if (getText() != other.getText()) {
                return SameResult("Not same text, '${getText()}' vs '${other.getText()}'.")
            }

            val sameData = sameData(this, other)
            if (!sameData.same) {
                return sameData
            }

            val sameChildren = sameChildren(this, other)
            if (!sameChildren.same) {
                return sameChildren
            }

            return SameResult()
        }

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
                val found = html.getChildren().map { child ->
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
        html.getHandler(name)(event)
    }

    fun hasAttribute(name: String) = html.hasAttribute(name)

    fun hasHandler(name: String) = html.hasHandler(name)

    fun getAttribute(name: String) = html.getAttribute(name)

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

data class SameResult(val message: String? = null) {
    val same = message == null
}