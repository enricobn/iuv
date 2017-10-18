package org.iuv.examples.buttons

import org.iuv.core.IUVApplication
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.PostServiceImpl

@JsName("ButtonsMain")
class ButtonsMain {
    fun run() {
        val application = IUVApplication(ButtonsIUV(1, PostServiceImpl()))
        application.run()
    }
}