package org.iuv.examples.buttons

import kotlinx.serialization.InternalSerializationApi
import org.iuv.core.IUVApplication
import org.iuv.core.SnabbdomRenderer

@JsName("ButtonsMain")
class ButtonsMain {

    @InternalSerializationApi
    fun run() {
        val application = IUVApplication(ButtonsView(1, PostServiceImpl()), SnabbdomRenderer())
        application.run()
    }

}