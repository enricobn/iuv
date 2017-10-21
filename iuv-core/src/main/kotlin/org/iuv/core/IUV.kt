package org.iuv.core

interface IUV<MODEL, MESSAGE> : UV<MODEL, MESSAGE> {

    fun init() : Pair<MODEL, Cmd<MESSAGE>>

}
