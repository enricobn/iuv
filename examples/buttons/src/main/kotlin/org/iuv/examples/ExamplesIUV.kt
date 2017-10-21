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

// Model
data class ExamplesModel(val buttonsModel: ButtonsModel, val gridModel: GridIUVModel, val currentIUV : IUV<*,*>?)

// Messages
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

        return Pair(ExamplesModel(buttonsModel, gridModel, null),
                Cmd.cmdOf(
                        buttonsCmd.map(::ButtonsIUVMessageWrapper),
                        gridCmd.map(::GridIUVMessageWrapper)))
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        when (message) {
            is LinkToButtons -> {
                Pair(model.copy(currentIUV = buttonsIUV), Cmd.none())
            }
            is LinkToGrid -> {
                Pair(model.copy(currentIUV = gridIUV), Cmd.none())
            }
            is ButtonsIUVMessageWrapper -> {
                val (childModel,childCmd) = buttonsIUV.update(message.buttonsIUVMessage, model.buttonsModel)
                Pair(model.copy(buttonsModel = childModel), childCmd.map(::ButtonsIUVMessageWrapper))
            }
            is GridIUVMessageWrapper -> {
                val (childModel,childCmd) = gridIUV.update(message.gridIUVMessage, model.gridModel)
                Pair(model.copy(gridModel = childModel), childCmd.map(::GridIUVMessageWrapper))
            }
            else -> {
                Pair(model, Cmd.none())
            }
        }

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            if (model.currentIUV == null) {
                div {
                    button {
                        +"Buttons"
                        onClick { _ -> LinkToButtons }
                    }
                }
                div {
                    button {
                        +"Grid"
                        onClick { _ -> LinkToGrid }
                    }
                }
            } else {
                when (model.currentIUV) {
                    is ButtonsIUV -> {
                        model.currentIUV.view(model.buttonsModel).map(this, ::ButtonsIUVMessageWrapper)
                    }
                    is GridIUV -> {
                        model.currentIUV.view(model.gridModel).map(this, ::GridIUVMessageWrapper)
                    }
                }
            }
        }
}