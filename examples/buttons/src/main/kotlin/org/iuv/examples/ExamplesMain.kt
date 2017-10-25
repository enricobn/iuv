package org.iuv.examples

import org.iuv.core.IUVApplication
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.grid.GridIUV

class ExamplesMain {

    fun run() {
        val router = IUVRouter()
        router.add("/", ExamplesIUV())
        router.add("/buttons", ButtonsIUV(1, PostServiceImpl()))
        router.add("/grid", GridIUV())

        val application = IUVApplication(router)
        application.run()
    }

}