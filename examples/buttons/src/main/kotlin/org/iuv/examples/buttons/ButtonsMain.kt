package org.iuv.examples.buttons

import org.iuv.core.IUVApplication
import org.iuv.core.SnabbdomRenderer

@JsName("ButtonsMain")
class ButtonsMain {

    fun run() {
        val application = IUVApplication(ButtonsIUV(1, PostServiceImpl()), SnabbdomRenderer())
        application.run()
    }

}