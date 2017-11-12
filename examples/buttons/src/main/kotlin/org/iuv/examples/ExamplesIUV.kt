package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.GotoMessage
import org.iuv.core.HTML
import org.iuv.core.IUV
import org.iuv.examples.buttons.ButtonsIUV
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.components.*
import org.iuv.examples.grid.GridIUV

// Model
data class ExamplesModel(val tabModel: TabModel)

// Messages
interface ExamplesMessage

data class ExamplesTabMessageWrapper(val message: TabMessage) : ExamplesMessage

private data class ExamplesGoto(override val path: String) : GotoMessage, ExamplesMessage

class ExamplesIUV : IUV<ExamplesModel, ExamplesMessage> {
    private val tab : Tab = Tab()

    companion object {

        private fun HTML<ExamplesMessage>.linkToButtons(id: Int) = link("Buttons $id", "buttons/$id")

        private fun HTML<ExamplesMessage>.link(text: String, url: String) {
            mtButton {
                +text
                onClick { ExamplesGoto(url) }
            }
        }

    }

    init {
        tab.add("Buttons", ButtonsIUV(1, PostServiceImpl()))
        tab.add("Grid", GridIUV)
    }

    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        val (tabModel,tabCmd) = tab.init()
        return Pair(ExamplesModel(tabModel), tabCmd.map(::ExamplesTabMessageWrapper))
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        when (message) {
            is ExamplesTabMessageWrapper -> {
                val (tabModel, tabCmd) = tab.update(message.message, model.tabModel)
                Pair(model.copy(tabModel = tabModel), tabCmd.map(::ExamplesTabMessageWrapper))
            }
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            vBox {
                linkToButtons(1)
                linkToButtons(2)
                link("Invalid buttons 3 with error", "buttons3")
                link("Grid", "grid")
                link("Not existent route", "notExistentRoute")
                link("Error", "buttons/hello")
                tab.view(model.tabModel).map(this, ::ExamplesTabMessageWrapper)
            }
        }

}