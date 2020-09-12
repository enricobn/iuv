package org.iuv.core

interface SubListener<in MESSAGE> {

    fun onMessage(message: MESSAGE)

}

private class SubNone<out MESSAGE> : Sub<MESSAGE> {

    override fun addListener(listener: SubListener<MESSAGE>) {
    }

    override fun removeListener(listener: SubListener<MESSAGE>) {
    }

    override fun removeListeners() {
    }
}

interface Sub<out MESSAGE> {

    companion object {
        private val none = SubNone<Any>()

        fun <MESSAGE> none() = none as Sub<MESSAGE>

        operator fun <MESSAGE> invoke(vararg subs: Sub<MESSAGE>) : Sub<MESSAGE> {
            val notNone = subs.filter { it !is SubNone }

            if (notNone.isEmpty()) {
                return none()
            }

            return object : Sub<MESSAGE> {
                override fun addListener(listener: SubListener<MESSAGE>) {
                    notNone.forEach { it.addListener(listener) }
                }

                override fun removeListener(listener: SubListener<MESSAGE>) {
                    notNone.forEach { it.removeListener(listener) }
                }

                override fun removeListeners() {
                    notNone.forEach { it.removeListeners() }
                }
            }
        }

        operator fun <MESSAGE> invoke(subs: List<Sub<MESSAGE>>) : Sub<MESSAGE> =
            invoke(*subs.toTypedArray())

        fun <MESSAGE,CONTAINER_MESSAGE> map(sub: Sub<MESSAGE>, map: (MESSAGE) -> CONTAINER_MESSAGE): Sub<CONTAINER_MESSAGE> {
            if (sub == none) {
                return none as Sub<CONTAINER_MESSAGE>
            }
            return object : Sub<CONTAINER_MESSAGE> {
                private var thisListener : SubListener<MESSAGE>? = null

                override fun addListener(listener: SubListener<CONTAINER_MESSAGE>) {
                    thisListener = object : SubListener<MESSAGE> {
                        override fun onMessage(message: MESSAGE) {
                            listener.onMessage(map(message))
                        }

                    }
                    thisListener.let {
                        sub.addListener(it!!)
                    }
                }

                override fun removeListener(listener: SubListener<CONTAINER_MESSAGE>) {
                    if (thisListener != null) {
                        thisListener.let {
                            sub.removeListener(it!!)
                        }
                    }
                }

                override fun removeListeners() {
                    if (thisListener != null) {
                        thisListener.let {
                            sub.removeListeners()
                        }
                    }
                }

            }
        }

    }

    fun addListener(listener: SubListener<MESSAGE>)

    fun removeListener(listener: SubListener<MESSAGE>)

    fun removeListeners()

}

class SubImpl<MESSAGE> : Sub<MESSAGE> {
    private val listeners = mutableSetOf<SubListener<MESSAGE>>()

    override fun addListener(listener: SubListener<MESSAGE>) {
        listeners.add(listener)
    }

    override fun removeListener(listener: SubListener<MESSAGE>) {
        listeners.add(listener)
    }

    override fun removeListeners() {
        listeners.clear()
    }

    fun dispatch(message: MESSAGE) {
        listeners.forEach { it.onMessage(message) }
    }
}