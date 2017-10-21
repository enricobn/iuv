package org.iuv.examples

import org.iuv.core.IUVApplication

class ExamplesMain {

    fun run() {
        val application = IUVApplication(ExamplesIUV())
        application.run()
    }

}