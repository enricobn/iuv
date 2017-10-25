package org.iuv.examples

import org.iuv.core.IUVApplication
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.ButtonsIUVMessage
import org.iuv.examples.buttons.ButtonsIUVModel
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.grid.GridIUV
import org.iuv.examples.grid.GridIUVMessage
import org.iuv.examples.grid.GridIUVModel

class ExamplesMain {

    fun run() {
        val mainIUV = ChildIUV<RouterModel, RouterMessage, ExamplesModel, ExamplesMessage>(
                ExamplesIUV(),
                ::RouterMessageWrapper,
                { it.childModels["/"] as ExamplesModel },
                { parentModel, childModel -> parentModel.copy(childModels = parentModel.childModels + ("/" to childModel)) }

        )
        val buttonsIUV = ChildIUV<RouterModel, RouterMessage, ButtonsIUVModel, ButtonsIUVMessage>(
                ButtonsIUV(1, PostServiceImpl()),
                ::RouterMessageWrapper,
                { (it.childModels["buttons"] as ButtonsIUVModel?)!! },
                { parentModel, childModel -> parentModel.copy(childModels = parentModel.childModels + ("buttons" to childModel)) }
        )
        val gridIUV = ChildIUV<RouterModel, RouterMessage, GridIUVModel, GridIUVMessage>(
                GridIUV(),
                ::RouterMessageWrapper,
                { it.childModels["grid"] as GridIUVModel },
                { parentModel, childModel -> parentModel.copy(childModels = parentModel.childModels + ("grid" to childModel)) }
        )

        val router = IUVRouter()
        router.add("/", mainIUV)
        router.add("/buttons", buttonsIUV)
        router.add("/grid", gridIUV)

        val application = IUVApplication(router)
        application.run()
    }

}