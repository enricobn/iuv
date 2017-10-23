package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.ButtonsIUVMessage
import org.iuv.examples.buttons.ButtonsIUVModel
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.grid.GridIUV
import org.iuv.examples.grid.GridIUVMessage
import org.iuv.examples.grid.GridIUVModel

// Model
data class ExamplesModel(val buttonsModel: ButtonsIUVModel?, val gridModel: GridIUVModel?,
                         val currentIUV : ChildUV<ExamplesModel,ExamplesMessage,*,*>?)

// Messages
interface ExamplesMessage

data class ButtonsIUVMessageWrapper(val buttonsIUVMessage: ButtonsIUVMessage) : ExamplesMessage

data class GridIUVMessageWrapper(val gridIUVMessage: GridIUVMessage) : ExamplesMessage

data class GoTo(val page: String) : ExamplesMessage

class ExamplesIUV : IUV<ExamplesModel, ExamplesMessage> {
    private val buttonsIUV = ChildIUV<ExamplesModel, ExamplesMessage, ButtonsIUVModel, ButtonsIUVMessage>(
        ButtonsIUV(1, PostServiceImpl()),
        ::ButtonsIUVMessageWrapper,
        { it.buttonsModel!! },
        { parentModel, childModel -> parentModel.copy(buttonsModel = childModel) }
    )
    private val gridIUV = ChildIUV<ExamplesModel, ExamplesMessage, GridIUVModel, GridIUVMessage>(
        GridIUV(),
        ::GridIUVMessageWrapper,
        { it.gridModel!! },
        { parentModel, childModel -> parentModel.copy(gridModel = childModel) }
    )
    private val children = mapOf("buttons" to buttonsIUV, "grid" to gridIUV)

    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        return Pair(ExamplesModel(null, null, null), Cmd.none())
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        when (message) {
            is GoTo -> {
                // TODO check that page exists
                val childIUV = children[message.page]!!
                val (newModel,cmd) = childIUV.init(model)
                Pair(newModel.copy(currentIUV = childIUV), cmd)
            }
            is ButtonsIUVMessageWrapper -> {
                buttonsIUV.update(message.buttonsIUVMessage, model)
            }
            is GridIUVMessageWrapper -> {
                gridIUV.update(message.gridIUVMessage, model)
            }
            else -> {
                Pair(model, Cmd.none())
            }
        }

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            if (model.currentIUV == null) {
                doMenu()
            } else {
                model.currentIUV.view(model, this)
            }
        }

    private fun HTML<ExamplesMessage>.doMenu() {
        div {
            button {
                +"Buttons"
                onClick { _ -> GoTo("buttons") }
            }
        }
        div {
            button {
                +"Grid"
                onClick { _ -> GoTo("grid") }
            }
        }
    }
}