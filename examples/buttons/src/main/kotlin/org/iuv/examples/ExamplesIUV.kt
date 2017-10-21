package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.ButtonsIUVMessage
import org.iuv.examples.buttons.ButtonsModel
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.grid.GridIUV
import org.iuv.examples.grid.GridIUVMessage
import org.iuv.examples.grid.GridIUVModel

data class ExamplesModel(val buttonsModel: ButtonsModel, val gridModel: GridIUVModel)

interface ExamplesMessage

data class ButtonsIUVMessageWrapper(val buttonsIUVMessage: ButtonsIUVMessage) : ExamplesMessage

data class GridIUVMessageWrapper(val gridIUVMessage: GridIUVMessage) : ExamplesMessage

object LinkToButtons : ExamplesMessage

object LinkToGrid : ExamplesMessage

class ExamplesIUV : IUV<ExamplesModel, ExamplesMessage> {
    private val buttonsIUV = ButtonsIUV(1, PostServiceImpl())
    private val gridIUV = GridIUV()

    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        val (buttonsModel, buttonsCmd) = buttonsIUV.init()
        val (gridModel, gridCmd) = gridIUV.init()

        return Pair(ExamplesModel(buttonsModel, gridModel),
                Cmd.cmdOf(
                        buttonsCmd.map(::ButtonsIUVMessageWrapper),
                        gridCmd.map(::GridIUVMessageWrapper)))
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        return Pair(model, Cmd.none())
    }

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            div {
                button { +"Buttons" }
            }
            div {
                button { +"Grid" }
            }
        }
}