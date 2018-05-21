package org.iuv.examples.grid

import org.iuv.core.IUVApplication
import org.iuv.core.IUVDebugger
import org.iuv.core.SnabbdomRenderer

class GridMain {

    fun run() {
        val application = IUVApplication(IUVDebugger(GridView), SnabbdomRenderer())
        application.run()
    }

}