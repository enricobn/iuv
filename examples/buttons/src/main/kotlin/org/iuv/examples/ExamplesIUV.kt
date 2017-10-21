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

object GoToButtons : ExamplesMessage

object GoToGrid : ExamplesMessage

class ExamplesIUV : IUV<ExamplesModel, ExamplesMessage> {
    private val buttonsIUV = ButtonsIUV(1, PostServiceImpl())
    private val gridIUV = GridIUV()

    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        // TODO I don't like to initialize the child IUV here, I don't want child commands to be fired here!
        val (buttonsModel, buttonsCmd) = buttonsIUV.init()
        val (gridModel, gridCmd) = gridIUV.init()

        return Pair(ExamplesModel(buttonsModel, gridModel, null),
            Cmd.cmdOf(
                    buttonsCmd.map(::ButtonsIUVMessageWrapper),
                    gridCmd.map(::GridIUVMessageWrapper)
            )
        )
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        when (message) {
            is GoToButtons -> {
                Pair(model.copy(currentIUV = buttonsIUV), Cmd.none())
            }
            is GoToGrid -> {
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
                        onClick { _ -> GoToButtons }
                    }
                }
                div {
                    button {
                        +"Grid"
                        onClick { _ -> GoToGrid }
                    }
                }
            } else {
                when (model.currentIUV) {
                    is ButtonsIUV -> {
                        // TODO if i forget to do the map I see nothing, the resulting html is not added to the parent!
                        model.currentIUV.view(model.buttonsModel).map(this, ::ButtonsIUVMessageWrapper)
                    }
                    is GridIUV -> {
                        // TODO if i forget to do the map I see nothing, the resulting html is not added to the parent!
                        model.currentIUV.view(model.gridModel).map(this, ::GridIUVMessageWrapper)
                    }
                }
            }
        }
}