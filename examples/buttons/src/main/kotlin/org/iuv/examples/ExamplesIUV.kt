package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.GotoMessage
import org.iuv.core.HTML
import org.iuv.core.IUV
import org.iuv.examples.simplecomponents.vBox

// Model
class ExamplesModel

// Messages
interface ExamplesMessage

private data class ExamplesGoto(override val url: String) : GotoMessage, ExamplesMessage

class ExamplesIUV : IUV<ExamplesModel, ExamplesMessage> {

    companion object {

        private fun HTML<ExamplesMessage>.linkToButtons(id: Int) = link("Buttons $id", "/buttons/$id")

        private fun HTML<ExamplesMessage>.link(text: String, url: String) {
            button {
                +text
                onClick { ExamplesGoto(url) }
            }
        }

    }

    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        return Pair(ExamplesModel(), Cmd.none())
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        Pair(model, Cmd.none())


    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            vBox {
                linkToButtons(1)
                linkToButtons(2)
                link("Invalid buttons 3 with error", "/buttons3")
                link("Grid", "/grid")
                link("Not existent route", "/notExistentRoute")
                link("Error", "/buttons/hello")
            }
        }

}