package org.iuv.core

interface View<MODEL, MESSAGE> : Component<MODEL, MESSAGE> {

    fun init() : Pair<MODEL, Cmd<MESSAGE>>

}
