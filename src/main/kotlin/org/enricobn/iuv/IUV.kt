package org.enricobn.iuv

interface IUV<MODEL, MESSAGE, CONTAINER_MESSAGE> : UV<MODEL, MESSAGE, CONTAINER_MESSAGE> {

    fun init() : MODEL

}