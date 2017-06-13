package org.enricobn.iuv

interface Subscription<EVENT,MESSAGE> {

    fun subscribe(map : (EVENT) -> MESSAGE) : Unit

}