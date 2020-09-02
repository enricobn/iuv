package org.iuv.core

import org.w3c.dom.events.Event

interface HTMLElementAttributes<MESSAGE> {

    fun addProperty(name: String, prop: dynamic)

    fun removeProperty(name: String)

    fun getProperty(key: String) : dynamic

    fun hasProperty(key: String) : Boolean

    fun <EVENT : Event> on(name: String, handler: (EVENT) -> MESSAGE?)

    fun <EVENT : Event> on(name: String, handler: (EVENT, MessageBus<MESSAGE>) -> Unit)

}