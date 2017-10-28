package org.iuv.examples

import org.iuv.core.IUVApplication
import org.iuv.core.IUVRouter
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.grid.GridIUV

class ExamplesMain {

    fun run() {
        val router = IUVRouter(ExamplesIUV())
        router.add("/buttons") { ButtonsIUV(it.first().toInt(), PostServiceImpl()) }
        router.add("/buttons1", ButtonsIUV(1, PostServiceImpl()))
        router.add("/grid", GridIUV())

        val application = IUVApplication(router)
        application.run()
    }

}