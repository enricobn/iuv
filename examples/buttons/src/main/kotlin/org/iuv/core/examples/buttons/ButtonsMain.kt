package org.iuv.core.examples.buttons

import org.iuv.core.IUVApplication
import org.iuv.core.snabbdom

@JsName("ButtonsMain")
class ButtonsMain {
    fun run() {
        val application = IUVApplication(ButtonsIUV(1, PostServiceImpl()))
        application.run()
    }
}