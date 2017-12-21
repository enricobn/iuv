package org.iuv.core

interface SubListener<in MESSAGE> {

    fun onMessage(message: MESSAGE)

}

private class SubNone<MESSAGE> : Sub<MESSAGE> {
    override fun addListener(listener: SubListener<MESSAGE>) {
    }

    override fun removeListener(listener: SubListener<MESSAGE>) {
    }

    override fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Sub<CONTAINER_MESSAGE> = Sub.none()

}

interface Sub<MESSAGE> {

    companion object {
        private val none = SubNone<Any>()

        fun <MESSAGE> none() = none as Sub<MESSAGE>

        operator fun <MESSAGE> invoke(vararg subs: Sub<MESSAGE>) : Sub<MESSAGE> {
            val notNone = subs.filter { it !is SubNone }

            if (notNone.isEmpty()) {
                return Sub.none()
            }

            return object : Sub<MESSAGE> {
                override fun addListener(listener: SubListener<MESSAGE>) {
                    notNone.forEach { it.addListener(listener) }
                }

                override fun removeListener(listener: SubListener<MESSAGE>) {
                    notNone.forEach { it.removeListener(listener) }
                }
            }
        }
    }

    fun addListener(listener: SubListener<MESSAGE>)

    fun removeListener(listener: SubListener<MESSAGE>)

    fun <CONTAINER_MESSAGE> map(map: (MESSAGE) -> CONTAINER_MESSAGE): Sub<CONTAINER_MESSAGE> {
        val self = this
        return object : Sub<CONTAINER_MESSAGE> {
            private var thisListener : SubListener<MESSAGE>? = null

            override fun addListener(listener: SubListener<CONTAINER_MESSAGE>) {
                thisListener = object : SubListener<MESSAGE> {
                    override fun onMessage(message: MESSAGE) {
                        listener.onMessage(map(message))
                    }

                }
                thisListener.let {
                    self.addListener(it!!)
                }
            }

            override fun removeListener(listener: SubListener<CONTAINER_MESSAGE>) {
                if (thisListener != null) {
                    thisListener.let {
                        self.removeListener(it!!)
                    }
                }
            }
        }
    }
}