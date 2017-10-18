package org.iuv.examples.grid

import org.iuv.core.IUVApplication

class GridMain {

    fun run() {
        val application = IUVApplication(GridIUV())
        application.run()
    }

}