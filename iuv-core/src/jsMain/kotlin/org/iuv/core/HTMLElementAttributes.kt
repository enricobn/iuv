package org.iuv.core

interface HTMLElementAttributes {

    fun addProperty(name: String, prop: dynamic)

    fun removeProperty(name: String)

    fun getProperty(key: String) : dynamic

    fun hasProperty(key: String) : Boolean

}